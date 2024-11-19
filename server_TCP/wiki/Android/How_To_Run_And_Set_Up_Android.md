### How to run the Android application:
Download the server_part4 folder and open it in Visual Studio Code (VSCode).
from this GitHub repository:[GitHub Repo](https://github.com/zivole/Server_youtube.git)

this folder contains the server code for the Android application, which is also adjusted to work with the TCP server.

after downloading the server you need to run this code: node server.js - and it needs to look like this:
![alt text](<Proof/Screenshot 2024-10-15 161510.png>)

Download the TCP.cpp file and put it in WSL (Windows Subsystem for Linux) in VSCode. Compile it using the following command:

```sh
g++ -std=c++11 -pthread main.cpp User.cpp VideoRecommendationSystem.cpp -o server

```
Then, run the TCP server using this command:

```sh
./server
```

like this:

![alt text](<Proof/Screenshot 2024-10-15 161644.png>)

If you are using a Mac, you can find additional instructions in the README file 



Now, download the Android application from this GitHub repository: [GitHub Repo](https://github.com/Guyrose1998/YouTube_android.git)

and open it using Android Studio. after set up the android code and run it need to look like this:

![alt text](<Proof/Screenshot 2024-10-18 142059.png>)

