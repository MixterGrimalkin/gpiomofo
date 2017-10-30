import opc, time
numLEDs = 64
client = opc.client('localhost:7890')

while True:
    for i in range(numLEDs):
        pixels = [ (0,0,0) ]*numLEDs
        pixels [i] = (100,200,0)
        client.put_pixels(pixels)
        time.sleep (0.1)