#!/bin/bash 
/home/pi/gst-rtsp-server/examples/./test-launch "( rpicamsrc preview=false bitrate=2000000 keyframe-interval=30 ! video/x-h264, framerate=30/1 ! h264parse ! rtph264pay name=pay0 pt=96 )"
