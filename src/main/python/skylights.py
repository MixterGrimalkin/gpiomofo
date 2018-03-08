# from audioplayer import AudioPlayer
# audio = AudioPlayer()
# import opc
# client = opc.client('localhost:7890')
import datetime, time, random, sys
from pixel_tape import *

LED_COUNT = 84

pixel_tape = PixelTape(LED_COUNT)

# Random factor to start lightening (in range 0-10 where 10 means always)
CHANCE_OF_LIGHTENING = 2

# Number of colour bands
# Randomly chosen from this range
MIN_BANDS = 2
MAX_BANDS = 8

# Milliseconds to sleep between renders (smaller number = faster)
# Randomly chosen from this range
MIN_SLEEP = 0.05
MAX_SLEEP = 0.12

# Seconds before changing palette/mode
SKY_TIME = 30
STORM_TIME = 10

# Colour palettes
# One is chosen at random on each change
# If there are more colours than bands, the first few colours are used
# If there are more bands than colours, the colour cycle is repeated
palettes = [
    [(255, 45, 10), (255, 30, 20), (200, 10, 0), (210, 26, 0)],  # Sunset Only
    [(200, 20, 0), (250, 90, 10), (50, 146, 179), (255, 84, 21), (255, 184, 21)],  # Sunset w. Blue Sky
    [(255, 161, 100), (48, 44, 93), (252, 144, 50), (252, 90, 27)],  # Sunset w. Lilac Sky
    [(30, 41, 56), (119, 145, 179), (10, 10, 10), (33, 40, 54)],  # Overcast Sky
    [(126, 217, 52), (15, 15, 15), (203, 6, 35), (15, 15, 15)],  # Aurora
]

TIMER_MODE = False

HOUR_SCATTER = 0
MINUTE_SCATTER = 29

TIMES = [
    [(0,0),(1,30)],
    [(7,0),(9,30)],
    [(12,0),(13,0)],
    [(17,00),(20,00)],
    [(22,0),(23,30)]
]

times = []

def scatter(value, range, modulus):
    return abs((value+random.randint(-range, range)) % modulus)

def scatter_times():
    global times
    times = [0]*len(TIMES)
    for i in range(len(TIMES)):
        on, off = TIMES[i]
        on_hrs = scatter(on[0], HOUR_SCATTER, 24)
        on_mins = scatter(on[1], MINUTE_SCATTER, 60)
        off_hrs = scatter(off[0], HOUR_SCATTER, 24)
        off_mins = scatter(off[1], MINUTE_SCATTER, 60)
        times[i] = [(min(on_hrs,off_hrs), min(on_mins,off_mins)), (max(off_hrs,on_hrs), max(off_mins,on_mins))]

    print times


# Audio files will be played at random during storm
audio_files = ["thunder1.mp3", "thunder2.mp3", "thunder3.mp3"]

sleep_time = 0.2
pixels = []
centres = []
deltas = []
palette = []
brightness = 0.0
delta_brightness = 0.1


def render():
    client_pixels = list(pixels)
    for i in range(len(pixels)):
        # print pixels[i]
        # client_pixels[i] = dim(pixels[i])
        pixel_tape.draw(i, dim(pixels[i]))
    # client.put_pixels(client_pixels)
    pixel_tape.render()

def reset():
    global sleep_time, centres, deltas, palette
    bands = random.randint(MIN_BANDS, MAX_BANDS)
    sleep_time = random.uniform(MIN_SLEEP, MAX_SLEEP)
    p = random.randint(0, len(palettes) - 1)
    palette = palettes[p] * bands
    centres = [0] * bands
    deltas = [1] * bands
    print "Palette:", p, "Bands:", bands
    c = 0
    for i in range(bands):
        new_centre = random.randint(0, LED_COUNT - 1)
        while new_centre in centres:
            new_centre = random.randint(0, LED_COUNT - 1)
        centres[c] = new_centre
        c += 1


def clear():
    global pixels
    pixels = [(0, 0, 0)] * LED_COUNT


def draw(pixel, rgb):
    pixels[pixel] = rgb


def dim(rgb):
    return rgb[0] * brightness, rgb[1] * brightness, rgb[2] * brightness


def interpolate(rgb1, rgb2, amount):
    result = [0, 0, 0]
    for i in range(3):
        val1, val2 = rgb1[i], rgb2[i]
        if val1 < val2:
            result[i] = val1 + ((val2 - val1) * (1.0 * amount))
        else:
            result[i] = val1 - ((val1 - val2) * (1.0 * amount))
    return result[0], result[1], result[2]


def centres_around(pixel):
    result = [-1, -1, -1]
    last_centre = -1
    for i in range(LED_COUNT):
        if i == pixel:
            if last_centre != -1:
                result[0] = last_centre
        if i in centres:
            last_centre = i
            if i > pixel and result[1] == -1:
                result[1] = last_centre
    if result[0] == -1:
        result[0] = last_centre
    if result[1] == -1:
        min_centre = centres[0]
        for i in range(len(centres)):
            if centres[i] < min_centre:
                min_centre = centres[i]
        result[1] = min_centre

    if result[0] > pixel:
        result[2] = 1.0 * (pixel + (LED_COUNT - result[0])) / (result[1] + (LED_COUNT - result[0]))
    elif result[1] < pixel:
        result[2] = 1.0 * (pixel - result[0]) / ((pixel - result[0]) + (LED_COUNT - pixel) + result[1])
    else:
        result[2] = 1.0 * (pixel - result[0]) / (result[1] - result[0])
    return result


def update_brightness():
    global brightness, delta_brightness
    brightness += delta_brightness
    if delta_brightness > 0.0 and brightness >= 1.0:
        brightness = 1.0
        delta_brightness = 0.0
    elif delta_brightness < 0.0 and brightness <= 0.0:
        brightness = 0.0
        delta_brightness = 0.0

def is_active():
    if not TIMER_MODE:
        return True

    for i in range(len(times)):
        on = datetime.time(times[i][0][0], times[i][0][1])
        off = datetime.time(times[i][1][0], times[i][1][1])
        if on <= datetime.datetime.now().time() < off:
            return True
    return False

def main():
    global delta_brightness
    has_cleared = False
    storm_mode = False
    reset()
    clear()
    render()
    scatter_times()
    start_time = time.time()

    while True:

        if is_active():

            if has_cleared:
                start_time = time.time()
                has_cleared = False

            update_brightness()
            t = time.time() - start_time
            if delta_brightness == 0.0 and ((not storm_mode and t > SKY_TIME) or (storm_mode and t > STORM_TIME)):
                start_time = time.time()
                if brightness >= 1.0:
                    delta_brightness = -0.1
                else:
                    delta_brightness = 0.1

            if brightness <= 0.0:
                start_time = time.time()
                reset()
                delta_brightness = 0.1
                storm_mode = (not storm_mode) and random.randint(0, 10) > (10 - CHANCE_OF_LIGHTENING)
                if storm_mode:
                    print "STORM!"
                # else:
                #     audio.stop()

            if storm_mode:
                clear()
                render()
                time.sleep(random.uniform(0.2, 1.5))
                flash = (random.randint(0, LED_COUNT), random.randint(2, 10))
                for i in range(flash[1]):
                    draw((flash[0] + i) % LED_COUNT, (255, 255, 255))
                render()
                # if not audio.is_playing():
                #     file = audio_files[random.randint(0,len(audio_files)-1)]
                #     print "Thunder: ", file
                #     audio.play(file)
                time.sleep(random.uniform(0.01, 0.2))

            else:
                clear()
                int_centres = list(centres)
                for i in range(len(centres)):
                    if pixels[(int(centres[i] + deltas[i]) % LED_COUNT)] == (0, 0, 0):
                        centres[i] = (centres[i] + deltas[i]) % LED_COUNT
                        draw(int(centres[i]), palette[i])
                        int_centres[i] = int(centres[i])
                    else:
                        deltas[i] *= -1
                for i in range(LED_COUNT):
                    if i not in int_centres:
                        ca = centres_around(i)
                        # draw(i, (0,0,0))
                        draw(i, interpolate(pixels[int(ca[0])], pixels[int(ca[1])], ca[2]))
                render()
                time.sleep(sleep_time)

        else:
            if not has_cleared:
                clear()
                render()
                scatter_times()
                has_cleared = True


if __name__ == "__main__":
    if len(sys.argv) > 1 and int(sys.argv[1]) < len(palettes):
        clear()
        brightness = 1.0
        for i in range(len(palettes[int(sys.argv[1])])):
            print i, palettes[int(sys.argv[1])][i]
            draw(i, palettes[int(sys.argv[1])][i])
        render()
    else:
        main()
