README
This program was written in Eclipse and has an Eclipse project file structure.
I am using Java 1.6 on this machine, although 1.7 should work fine.

I programmed this on OSX, but used a Windows 7 machine as the other client. For UDP, everything works great from both ends. For TCP, something seems wrong from Windows->OSX, but OSX->Windows voice sounds okay (minus some clicking).  UDP is programmed with Java's DatagramSocket API, while TCP is Java's Socket API.

•   All source is under /src/speech_over_ip/*.java
•   Compiled *.class files go under /bin/speech_over_ip

Speak requires 6 parameters to run. Parameters 7 and 8 are optional. If no ITL/ITU are given, they are inferred based
on the latency parameter by assuming "silence" meant each byte's integer value is '1'. For example, with 20ms latency
each chunk will be 160bytes, and thus the energy is <= 160 for silence. However, if that logic is poor, ITL/ITU can be
passed in to the program as parameters as previously mentioned.

1. Host: String          - IP address or hostname
2. Port: Int             - Port number to talk on
3. Loss: Int             - Percentage of packets to drop on the receiver side to simulate network loss
4. Latency: Int          - Integer from 20-1000 for how many milliseconds of audio the sender should gather before sending.
5. Packet type: String   - TCP or UDP
6. Detect speech: Bool   - 'true' or 'false' to not send audio chunks with energy lower than ITU
- - - - - Optional Params Below - - - - -
7. ITL: Int              - ITL lower threshold for speech energy
8. ITU: Int              - ITU upper threshold for speech energy


Execute these commands inside the root project directory (where this README sits):

    Compile:    javac -d bin src/speech_over_ip/*.java

    Run:        java -cp ./bin speech_over_ip.Main [PLUS PARAMETERS]




Written by Adam Veres
For CS529 Multimedia Networking
Project 2
Spring 2014, WPI
