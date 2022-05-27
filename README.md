

<!-- omit in toc -->
# **DL4JRA**
<a href="https://github.com/msf4-0/DL4JRA/blob/main/LICENSE">
    <img alt="GitHub" src="https://img.shields.io/github/license/msf4-0/DL4JRA.svg?color=blue">
</a>
<a href="https://github.com/msf4-0/DL4JRA/issues">
      <img alt="Issues" src="https://img.shields.io/github/issues/msf4-0/DL4JRA?color=blue" />
</a>
<a href="https://github.com/msf4-0/DL4JRA/releases">
    <img alt="Releases" src="https://img.shields.io/github/release/msf4-0/DL4JRA?color=success" />
</a>
<a href="https://github.com/msf4-0/DL4JRA/releases">
    <img alt="Downloads" src="https://img.shields.io/github/downloads/msf4-0/DL4JRA/total.svg?color=success" />
</a>
<a href="https://github.com/msf4-0/DL4JRA/pulls">
    <img alt="GitHub pull requests" src="https://img.shields.io/github/issues-pr/msf4-0/DL4JRA?color=blue" />
</a>

A no-code app for the training of custom machine learning models in Java using DeepLearning4J.
<i>Note: the app is currently in open beta, and is still undergoing active development. If you encounter a bug, please file an issue [here](https://github.com/msf4-0/DL4JRA/issues). </i>
<br>

Features include:
1. Image dataset generation 
2. Highly customizable model building and retraining of pretrained models using an easy-to-use drag and drop interface for:
	- Image classification 
	- Object detection
	- Image segmentation
	- Csv classification
3. Model testing and deployment for object detection and image classification
4. Ability to route video output through the MQTT protocol

https://user-images.githubusercontent.com/72961684/165240897-55f93f8e-70a3-4897-bafd-59929143901b.mp4


This software is licensed under the [GNU GPLv3 LICENSE](/LICENSE) © [Selangor Human Resource Development Centre](http://www.shrdc.org.my/). 2021.  All Rights Reserved. Users that want to modify and distribute versions of DL4JRA and do not wish to conform to obligations to share the source code are free to contact SHRDC for alternative licensing options.


<!-- omit in toc -->
## **Basic Installation**
Tested to work on Windows and Linux
- [x] Download and install [Git](https://git-scm.com/)
- [x] Clone the repository from github by running ```git clone https://github.com/msf4-0/DL4JRA``` 
- [x] Go to [Oracle Java Archive](https://www.oracle.com/java/technologies/javase/javase8-archive-downloads.html) and install Java SE 8 (Used as project SDK). Remember to [set up enviroment variables](https://mkyong.com/java/how-to-set-java_home-on-windows-10/) after downloading Java SE 8
- [x] Download and install [Node.js](https://nodejs.org/en/download/)
- [x] Download [Apache Maven](https://maven.apache.org/download.cgi). Installation steps for window [here](https://docs.wso2.com/display/IS323/Installing+Apache+Maven+on+Windows)
- [x] Navigate to "client" directory and install all dependencies

```
> cd DL4JRA/client
> npm install
```
<br>

<!-- omit in toc -->
## **Launching the Application**
<!-- omit in toc -->
### **Server (SpringBoot)**
```
> cd DL4JRA/server
> mvn spring-boot:run
```
<!-- omit in toc -->
### **Client (ReactJS)**
```
> cd DL4JRA/client
> npm start
```

<!-- omit in toc -->

<br>
<!-- omit in toc -->

## **Contributing**

We welcome any and all contributions through pull requests, whether it be bug fixes or new features. 

<!-- omit in toc -->
### **Installation steps for development**
- [x] Download and install [IntelliJ](https://www.jetbrains.com/idea/download/#section=windows) IDE for server
- [x] Clone the repository from github inside IntelliJ (open from VSC)
- [x] Download and install [Visual Code Studio](https://code.visualstudio.com/) IDE for ReactJS code
- [x] Go to [Oracle Java Archive](https://www.oracle.com/java/technologies/javase/javase8-archive-downloads.html) and install Java SE 8 (Used as project SDK). [Set up enviroment variables](https://mkyong.com/java/how-to-set-java_home-on-windows-10/) after downloading JAVA SE 8
- [x] Download [OpenCV 4.5.3](https://opencv.org/opencv-4-5-3/). Install [OpenCV in IntelliJ](https://medium.com/@aadimator/how-to-set-up-opencv-in-intellij-idea-6eb103c1d45c)
- [x] Download and install [Node.js](https://nodejs.org/en/download/)
- [x] Download [Apache Maven](https://maven.apache.org/download.cgi). Installation steps for window [here](https://docs.wso2.com/display/IS323/Installing+Apache+Maven+on+Windows)
- [x] Navigate to "client" directory and install all dependencies

## **DL4JRA Guide**
A comprehensive introduction to DL4JRA! Portal to [DL4JRA wiki](https://github.com/msf4-0/DL4JRA/wiki). 
