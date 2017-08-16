# WebRTC-for-Unity (WIP, In development)
WebRTC for Unity (WIP, In development)
This repo aims to facilitate WebRTC development in Unity, and maybe have a unifed API that behaves the same on different platforms.

## Current state:
Only for android.

The context needs to be an **GLES2** for now. Which means, in player settings, Auto Graphics API needs to be unchecked, and only the OpenGLES2 option is selected. This is due to the implementation of the EGL context on webrtc. I have submitted an issue to the webrtc repo, and proposed an fix.

The image is grabed through an external texture, Blit to a RenderTexture with a shader for external textures (similar to the VideoDecodeAndroid shader), and applied to the material.

Demo scene shows capture from camera, and screen share, from webrtc video capturer to unity, rendering to multiple Game Objects.

<img src="https://dl2.pushbulletusercontent.com/Xj8v2Wliajvr8PYRvIdKS2Yu7PlqT2PP/Screenshot_20170816-145027.png" width="500" />

## Roadmap:
- [x] Create an android plugin 
- [x] Create an proxy to move I420 frames between the WebRTC and Unity (Textures)
    -   Creating a video capturer (Camera/Screenshare) and have it send a texture to Unity for rendering
    -   This would allow later to either stream the the video, or save it to disk (since WebRTC have everything needed to support encoding/decoding).
    -   Maybe have more options and flexibilites over this system (create a video stream from a render texture, <s>hardware acceleration option; aka whether to use the GLES texture, or the YUV data of the frame</s> )
- [ ] Get a simple video call POC in Unity
- [ ] Clean up code
- [ ] Create an abstracted WebRTC API for Unity, that can be implemented for each platform
- [ ] Support for iOS within the same API
- [ ] Support for WebGL (jslib plugin + polyfill maybe)
- [ ] Support for Standalone (Windows, OS X, Linux)
- [ ] Support for Editor (Should be easy, if standalone works)
- [ ] Create an interface API for signaling, that can be implemented for different ways of signaling (Websocket/Socket, Http, etc)

## Resources:
The official webrtc repo contains a unity plugin example, which is worth investigating.
https://chromium.googlesource.com/external/webrtc.git/+/master/webrtc/examples/unityplugin/

## Contribution:
Yes, please. If you think you can contribute to any of the points above, or have any suggestions, design thoughts, use cases, anything really, go ahead and open an issue and/or create a pull request.
