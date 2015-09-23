# Sing-it-now

Sing it now is a Karaoke game developed during Hack the North. It consists of two components,
 - Java Server and Display on PC
 - Microphone, our android App.

Real time audio recorded through the Android devices microphone is streamed using Datagrams directly to our server. Smaller the network, better the latency. The audio received in the Java Server is mixed with the background karaoke music and sent to the speaker. Multiple devices can be connected to our Server. The front end powered by LibGDX parses the lyric file and displays the lyrics corresponding to the song on the display. To select a song from the library to be played, the Server accepts input though the phone. The touch input from the phone is sent as a 5 digit code which is assigned to the song details in our database. This song is then retried from the server and played. To connect the phone to the server, we need to input the IP Address. To improved user experience, the IP is uploaded to our server and is given a 4 digit code when the room is created. The user needs to just input this code and the connection is made automatically to the server.

Technologies used were Android, Java, LibGDX (Java Game Engine for front end), Sockets, Datagrams, PHP and SQL.

#Navigation

Server without Frontend -> Java Server without frontend
LibGDX -> Java Server with front end powered by LibGDX
Android Client -> Android Client which pairs to the server

#Screenshots

PC
![Scr1](https://github.com/Abhiseshan/Sing-it-now/blob/master/Screenshots/scr1.jpg)
![Scr2](https://github.com/Abhiseshan/Sing-it-now/blob/master/Screenshots/scr2.jpg)
![Scr3](https://github.com/Abhiseshan/Sing-it-now/blob/master/Screenshots/scr3.jpg)
![Scr4](https://github.com/Abhiseshan/Sing-it-now/blob/master/Screenshots/scr4.jpg)
![Scr5](https://github.com/Abhiseshan/Sing-it-now/blob/master/Screenshots/scr5.jpg)

Android
![Scr1_phone](https://github.com/Abhiseshan/Sing-it-now/blob/master/Screenshots/phone_scr1.png)
![Scr2_phone](https://github.com/Abhiseshan/Sing-it-now/blob/master/Screenshots/phone_scr2.png)
![Scr3_phone](https://github.com/Abhiseshan/Sing-it-now/blob/master/Screenshots/phone_scr3.png)
![Scr4_phone](https://github.com/Abhiseshan/Sing-it-now/blob/master/Screenshots/phone_scr4.png)
![Scr5_phone](https://github.com/Abhiseshan/Sing-it-now/blob/master/Screenshots/phone_scr5.png)
