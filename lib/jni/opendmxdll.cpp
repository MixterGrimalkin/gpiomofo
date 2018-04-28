//---------------------------------------------------------------------------

#include <windows.h>
#include <process.h>
#include <STDDEF.H>
#include <vcl.h>
#include "com_juanjo_openDmx_OpenDmx.h"
#include "ftd2xx.h"

#define OPENDMXUSB_OK 0
#define OPENDMXUSB_OPEN_ERROR 1
#define OPENDMXUSB_DEVICE_ERROR 2
#define OPENDMXUSB_PARAMETER_ERROR 3
#define OPENDMXUSB_NOT_INITIALIZED 4
#define OPENDMXUSB_ALREADY_INITIALIZED 5
#define OPENDMXUSB_THREAD_START 6
#define OPENDMXUSB_TIMER_RESOLUTION 7

#define OPENDMXUSB_MODE_TX 0
#define OPENDMXUSB_MODE_RX 1


/////////////////////////
void DmxTransmit(void *);
void DmxReceive(void *);

unsigned char dmxBuffer[513];  //includes Start byte so Chaneel 1 is dmxBuffer[1]
int killThread=FALSE;
unsigned long byteCount=0;
unsigned long frameCount=0;
unsigned short maxChannels=512;
unsigned short rxFrameLength;
unsigned short framesToAverage=5;
int initialized=FALSE;
int threadActive=FALSE;
FT_HANDLE hDevice=NULL;

CRITICAL_SECTION cs;
unsigned long pThread;
float frameRate[10];
LARGE_INTEGER clockFrequency;
LARGE_INTEGER breakTicks;
unsigned long frameTime=(long)(1000000.0/25); //Initial frame rate 25
unsigned long emptyFrames=0;
////////////////////////////

#pragma hdrstop
#pragma argsused

BOOL WINAPI DllMain(HINSTANCE hinstDLL, DWORD fwdreason, LPVOID lpvReserved)
{
    return 1;
}

//---------------------------------------------------------------------------
//---------------------------------------------------------------------------
//---------------------------------------------------------------------------

//conectar
JNIEXPORT jboolean JNICALL Java_com_juanjo_openDmx_OpenDmx_connect
  (JNIEnv *_enc, jclass _class,jint _mode){

    char buffer[64];
	long status;
	FTDCB ftDCB;
	FTTIMEOUTS ftTS;
	int index;

    //INICIAR MODO
	if(_mode!=OPENDMXUSB_MODE_TX && _mode!=OPENDMXUSB_MODE_RX)
		return false;
	if(initialized)
		return false;

	QueryPerformanceFrequency(&clockFrequency);
	breakTicks.QuadPart=(long)(100.0*clockFrequency.QuadPart/1000000.0);

	for(index=0;index<10;frameRate[index++]=0);

   	byteCount=frameCount=0;
	killThread=0;


    //INICIAR DISPOSITIVO
	//Get device string of first connected device
	if((status=FT_ListDevices(0,buffer,FT_LIST_BY_INDEX|FT_OPEN_BY_DESCRIPTION))!=FT_OK)
		return false;

	//Open device
	if(_mode==OPENDMXUSB_MODE_TX)
	{
		if((hDevice=FT_W32_CreateFile(buffer,GENERIC_WRITE,0,NULL,OPEN_EXISTING,FILE_ATTRIBUTE_NORMAL|FT_OPEN_BY_DESCRIPTION,NULL))==INVALID_HANDLE_VALUE)
           return false;
	}
	else
	{
		if((hDevice=FT_W32_CreateFile(buffer,GENERIC_READ,0,NULL,OPEN_EXISTING,FILE_ATTRIBUTE_NORMAL|FT_OPEN_BY_DESCRIPTION|FILE_FLAG_OVERLAPPED,NULL))==INVALID_HANDLE_VALUE)
            return false;
	}

	//Set comms parameters

	if(!FT_W32_GetCommState(hDevice,&ftDCB))
		return false;

	ftDCB.BaudRate=250000L;
	ftDCB.ByteSize=8;
	ftDCB.StopBits=2;
	ftDCB.Parity=0;
	ftDCB.fOutX=0;
    ftDCB.fInX=0;
    ftDCB.fErrorChar=0;
    ftDCB.fBinary=1;
    ftDCB.fRtsControl=0;
    ftDCB.fAbortOnError=0;

	if(!FT_W32_SetCommState(hDevice,&ftDCB))
		return false;

	//Set timeouts to 1 second

	ftTS.ReadIntervalTimeout = 0;
	ftTS.ReadTotalTimeoutMultiplier = 0;
	ftTS.ReadTotalTimeoutConstant = 1000;
	ftTS.WriteTotalTimeoutMultiplier = 0;
	ftTS.WriteTotalTimeoutConstant = 1000;

	if(!FT_W32_SetCommTimeouts(hDevice,&ftTS))
		return false;

	//Purge buffers
	if(!FT_W32_PurgeComm(hDevice,FT_PURGE_RX|FT_PURGE_TX))
		return false;

	if(_mode==OPENDMXUSB_MODE_TX)
	{
		//Clear RTS to put device in transmit mode
		if(!FT_W32_EscapeCommFunction(hDevice,CLRRTS))
			return false;
	}
	else
	{
		//Set RTS to put device in transmit mode
		FT_W32_EscapeCommFunction(hDevice,SETRTS);
		//Monitor break events in receive mode
		FT_W32_SetCommMask(hDevice,EV_ERR);
	}

    //INICIAR HILOS
    if(timeBeginPeriod(1)==TIMERR_NOCANDO)
		return false;
	InitializeCriticalSection(&cs);
	if(_mode==OPENDMXUSB_MODE_TX)
		pThread=_beginthread(DmxTransmit,0,NULL);
	else
		pThread=_beginthread(DmxReceive,0,NULL);
	if(pThread==-1)
	{
		FT_W32_CloseHandle(hDevice);
		DeleteCriticalSection(&cs);
		return false;
	}
	SetThreadPriority((HANDLE)pThread,THREAD_PRIORITY_TIME_CRITICAL);
	initialized=TRUE;

	return true;
}

//desconectar
JNIEXPORT jboolean JNICALL Java_com_juanjo_openDmx_OpenDmx_disconnect
  (JNIEnv *_env, jclass _class){

   if(!initialized)
		return true;

	killThread=TRUE;
	Sleep(500);

	while(threadActive);

	DeleteCriticalSection(&cs);
	timeEndPeriod(1);
	initialized=FALSE;
	memset(dmxBuffer,0,sizeof(dmxBuffer));

	if(!FT_W32_CloseHandle(hDevice))
		return false;

	return true;

}


//poner un canal a un valor
JNIEXPORT void JNICALL Java_com_juanjo_openDmx_OpenDmx_setValue
  (JNIEnv *_env, jclass _class, jint _channel, jint _value){

    if(_channel<0)_channel=0;
    if(_channel>511)_channel=511;
    dmxBuffer[_channel+1]=_value;
}

//obtener el valor de un canal
JNIEXPORT jint JNICALL Java_com_juanjo_openDmx_OpenDmx_getValue
  (JNIEnv *_env, jclass _class, jint _channel){

    if(_channel<0)_channel=0;
    if(_channel>511)_channel=511;
    return (jint)dmxBuffer[_channel+1];
}

//---------------------------------------------------------------------------
//---------------------------------------------------------------------------
//---------------------------------------------------------------------------

//hilo de envio
void DmxTransmit(void * param)
{
	unsigned long bytes;
	int i;
	long delay;
	LARGE_INTEGER t,start,last,target;
	LONG elapsedTime; //micro seconds
	threadActive=TRUE;
	QueryPerformanceCounter(&last);
	while(1)
	{
		EnterCriticalSection(&cs);
		if(killThread)
			break;
		for(i=0;i<framesToAverage-1;i++)
			frameRate[i]=frameRate[i+1];
		frameRate[framesToAverage-1]=0;
		QueryPerformanceCounter(&t);
		elapsedTime=(long)(1000000*(t.QuadPart-last.QuadPart)/clockFrequency.QuadPart);
		delay=frameTime-elapsedTime;
		if(delay>0)
		{
			target.QuadPart=(LONGLONG)(t.QuadPart+clockFrequency.QuadPart*(delay)/(double)1000000);
			if(delay>1000)
				Sleep((DWORD)(delay/1000.0));
			QueryPerformanceCounter(&t);
			while(t.QuadPart<target.QuadPart)
				QueryPerformanceCounter(&t);
		}
		QueryPerformanceCounter(&start);
		frameRate[framesToAverage-1]=clockFrequency.QuadPart/(double)(start.QuadPart-last.QuadPart);
		last.QuadPart=start.QuadPart;
		if(!FT_W32_SetCommBreak(hDevice))
		{
			frameRate[framesToAverage-1]=0;
			LeaveCriticalSection(&cs);
			continue;		
		}
		if(!FT_W32_ClearCommBreak(hDevice))
		{
			frameRate[framesToAverage-1]=0;
			LeaveCriticalSection(&cs);
			continue;		
		}

		if(!FT_W32_WriteFile(hDevice,dmxBuffer,513L,&bytes,NULL) )
		{
			frameRate[framesToAverage-1]=0;
			LeaveCriticalSection(&cs);
			continue;		
		}
		byteCount+=bytes;
		frameCount++;
		LeaveCriticalSection(&cs);
	}
	LeaveCriticalSection(&cs);
	threadActive=FALSE;
}

//hilo de recepcion
void DmxReceive(void * param)
{	
	unsigned long bytesToRead,bytesRead;
	unsigned long events;
	DWORD currentErrors;
	DWORD lastErrors=0;
	COMSTAT comStat;
	OVERLAPPED os;
	HANDLE hEvent;
	
	unsigned char buffer[513],tmpBuffer[513];
	LARGE_INTEGER t,start,last;
	LONG elapsedTime; //micro seconds
	int i;
	memset(buffer,0,sizeof(buffer));
	QueryPerformanceCounter(&last);
	hEvent=CreateEvent(NULL,FALSE,TRUE,"");
	FT_W32_SetCommMask(hDevice,EV_ERR);
	QueryPerformanceCounter(&start);
	while(1)
	{
		EnterCriticalSection(&cs);
		if(killThread)
			break;
		LeaveCriticalSection(&cs);
		os.hEvent=hEvent;	
		os.Offset=0;
		os.OffsetHigh=0;

		if(!FT_W32_WaitCommEvent(hDevice,&events,&os)==0)
		{
			if (FT_W32_GetLastError(hDevice)==ERROR_IO_PENDING)
			{ 
				while(!killThread)
				{
					if(FT_W32_GetOverlappedResult(hDevice, &os, &bytesRead,FALSE))
						break;
					Sleep(1);
				}
			} 
			else
			{
				Sleep(1);
				continue;
			}
		} 

		if(!FT_W32_ClearCommError(hDevice, &currentErrors, (FTCOMSTAT *)&comStat))
		{
			Sleep(1);
			continue;
		}
		if(currentErrors&&currentErrors==lastErrors)
		{
			Sleep(1);
			continue;
		}
		lastErrors=currentErrors;

	
		if(currentErrors & CE_BREAK)
		{
			last.QuadPart=start.QuadPart;
			EnterCriticalSection(&cs);
			for(i=0;i<framesToAverage-1;i++)
				frameRate[i]=frameRate[i+1];
			QueryPerformanceCounter(&start);	
			frameRate[framesToAverage-1]=clockFrequency.QuadPart/(double)(start.QuadPart-last.QuadPart);
			memcpy(dmxBuffer,buffer,sizeof(dmxBuffer));
			LeaveCriticalSection(&cs);	
			continue;
		}
		else if(currentErrors)
		{
			FT_W32_PurgeComm(hDevice,FT_PURGE_RX);
			Sleep(1);
			continue;
		}	

		if(comStat.cbInQue==0)
		{
			emptyFrames++;
			QueryPerformanceCounter(&t);
			elapsedTime=(long)(1000000*(t.QuadPart-last.QuadPart)/clockFrequency.QuadPart);
			if(elapsedTime>1000000)  
			{
				EnterCriticalSection(&cs);
				for(i=0;i<framesToAverage-1;i++)
					frameRate[i]=frameRate[i+1];
				frameRate[framesToAverage-1]=0;
				rxFrameLength=0;
				memset(buffer,0,sizeof(buffer));
				memcpy(dmxBuffer,buffer,sizeof(dmxBuffer));
				FT_W32_PurgeComm(hDevice,FT_PURGE_RX);
				LeaveCriticalSection(&cs);
			}
			Sleep(1);
			continue;
		}
		bytesToRead=(sizeof(tmpBuffer)<comStat.cbInQue)?sizeof(tmpBuffer):comStat.cbInQue;
		if(bytesToRead<25)
		{
			FT_W32_PurgeComm(hDevice,FT_PURGE_RX);
			Sleep(1);
			continue;
		}

		memset(tmpBuffer,0,sizeof(tmpBuffer));

		os.hEvent=hEvent;
		os.Offset=0;
		os.OffsetHigh=0;
 		if(FT_W32_ReadFile(hDevice, tmpBuffer,bytesToRead,&bytesRead, &os))
		{
			if(bytesToRead==bytesRead)
			{
				rxFrameLength=bytesRead-1;
				memcpy(buffer,tmpBuffer,sizeof(buffer));
			}
			FT_W32_PurgeComm(hDevice,FT_PURGE_RX);
			continue;
		}
		
		if(FT_W32_GetLastError(hDevice)==ERROR_IO_PENDING)
		{
			while(!killThread)
			{
				if(FT_W32_GetOverlappedResult(hDevice, &os, &bytesRead, FALSE))
				{
					if(bytesToRead==bytesRead)
					{
						rxFrameLength=bytesRead-1;
						memcpy(buffer,tmpBuffer,sizeof(buffer));
					}
					FT_W32_PurgeComm(hDevice,FT_PURGE_RX);
					break;
				}
				Sleep(1);
			}
		}
	}
	LeaveCriticalSection(&cs);
	CloseHandle(hEvent);
	threadActive=FALSE;
}


