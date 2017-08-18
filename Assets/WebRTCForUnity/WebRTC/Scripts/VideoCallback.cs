using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System;


namespace iBicha
{
	public class VideoCallback : AndroidJavaProxy {
		public event Action<AndroidJavaObject, AndroidJavaObject> OnVideoCapturerStarted;
		public event Action<Texture> OnTexture;
		public event Action OnVideoCapturerStopped;
		public event Action<string> OnVideoCapturerError;

		private Texture2D nativeTexture;
		private RenderTexture rTexture;

		private Material _videoDecodeMaterial;
		private Material VideoDecodeMaterial{
			get {
				if (_videoDecodeMaterial == null) {
					// Hidden/VideoDecodeAndroid shader simply doesn't want to vertical flip, even with texture scale and offset.
					//It also doesn't rotate, so, custom shader it is.
					_videoDecodeMaterial = Resources.Load<Material> ("VideoDecodeMaterial");
				}
				return _videoDecodeMaterial;
			}
		}

		private const int OESExternal_To_RGBA_PassIndex = 0;
		private const int FlipV_OESExternal_To_RGBA_PassIndex = 1;
		private const int Rotate_FlipV_OESExternal_To_RGBA_PassIndex = 2;

		private int usePass;

		private int width;
		private int height;
		private int rotation;

		private float resolution = 1f;

		public VideoCallback (float resolution = 1f) : base ("com.ibicha.webrtc.VideoCallback")
		{
			this.resolution = Mathf.Clamp01(resolution);
		}

		public void onVideoCapturerStarted (AndroidJavaObject videoCapturer, AndroidJavaObject videoTrack)
		{
			ThreadUtils.RunOnUpdate (() => {
				Action<AndroidJavaObject, AndroidJavaObject> OnVideoCapturerStartedHandler = OnVideoCapturerStarted;
				if (OnVideoCapturerStartedHandler != null) {
					OnVideoCapturerStartedHandler (videoCapturer, videoTrack);
				}
			});
		}


		public void renderFrame (int width, int height, int rotation, int textureName, AndroidJavaObject i420Frame)
		{

			ThreadUtils.RunOnUpdate (() => {
				IntPtr textureId = new IntPtr (textureName);
				if (nativeTexture == null || this.width != width || this.height != height || this.rotation != rotation) {
					CleanUp();
					this.width = width;
					this.height = height;
					this.rotation = rotation;
					nativeTexture = Texture2D.CreateExternalTexture (width, height, TextureFormat.YUY2, false, false, textureId);
					rTexture = new RenderTexture (Mathf.RoundToInt(width * resolution), Mathf.RoundToInt(height * resolution), 0, RenderTextureFormat.RGB565);
					rTexture.dimension = UnityEngine.Rendering.TextureDimension.Tex2D;

					//To optimize, if rotation is zero, we use a different pass, to avoid calculating all the cos and sin.
					usePass = rotation == 0 ? FlipV_OESExternal_To_RGBA_PassIndex : Rotate_FlipV_OESExternal_To_RGBA_PassIndex;
					VideoDecodeMaterial.SetFloat("rotation",  -(float)rotation * Mathf.Deg2Rad);

					Action<Texture> OnTextureHandler = OnTexture;
					if (OnTextureHandler != null) {
						OnTextureHandler (rTexture);
					}

				} else {
					nativeTexture.UpdateExternalTexture (textureId);
				}

				Graphics.Blit (nativeTexture, rTexture, VideoDecodeMaterial, usePass);
				WebRTCAndroid.KillFrame (i420Frame);
			});
		}

		public void onVideoCapturerStopped ()
		{
			ThreadUtils.RunOnUpdate (() => {
				CleanUp ();
				Action OnVideoCapturerStoppedHandler = OnVideoCapturerStopped;
				if (OnVideoCapturerStoppedHandler != null) {
					OnVideoCapturerStoppedHandler ();
				}
			});
		}

		public void onVideoCapturerError (string error)
		{
			ThreadUtils.RunOnUpdate (() => {
				CleanUp ();
				Action<string> OnVideoCapturerErrorHandler = OnVideoCapturerError;
				if (OnVideoCapturerErrorHandler != null) {
					OnVideoCapturerErrorHandler (error);
				}
			});
		}

		void CleanUp ()
		{
			if (nativeTexture != null) {
				GameObject.Destroy (nativeTexture);
				nativeTexture = null;
			}
			if (rTexture != null) {
				rTexture.Release ();
				GameObject.Destroy (rTexture);
				rTexture = null;
			}
		}

	}

}
