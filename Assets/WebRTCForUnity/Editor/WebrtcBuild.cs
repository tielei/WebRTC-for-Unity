using UnityEngine;
using UnityEditor;
using System;

public class WebrtcBuild : EditorWindow {

	[MenuItem ("Window/WebRTC Build")]
	static void Init ()
	{ 
		// Get existing open window or if none, make a new one:
		WebrtcBuild window = (WebrtcBuild)EditorWindow.GetWindow (typeof(WebrtcBuild));
		window.titleContent.text = "WebRTC Build";
		window.Show();
	}

	void OnGUI()
	{
		EditorGUILayout.PrefixLabel ("Android");
		if (GUILayout.Button("Rebuild WebRTC library", GUILayout.MinWidth(110)))
		{
			GradleBuild ();
		}
		EditorGUILayout.PrefixLabel ("SocketIO");
		EditorGUILayout.BeginHorizontal ();
		if (GUILayout.Button("Run example server", GUILayout.MinWidth(110)))
		{
			RunSocketIOServer ();
		}
		if (GUILayout.Button("npm install", GUILayout.Width(100)))
		{
			InstallServerDependencies ();
		}
		EditorGUILayout.EndHorizontal ();
	}


	public static bool GradleBuild() {
		string androidLocation = System.IO.Path.GetFullPath("./webrtc-android/");
		if (System.IO.Directory.Exists(androidLocation))
		{
			System.Diagnostics.Process gradle = new System.Diagnostics.Process();
			gradle.StartInfo.FileName = "gradlew";
			gradle.StartInfo.Arguments = "assembleRelease";
			gradle.StartInfo.WorkingDirectory = androidLocation;
			//TODO: hook stdout to unity console
			if (gradle.Start())
			{
				if (gradle.WaitForExit(60 * 1000))
				{
					if(gradle.ExitCode != 0)
					{
						Debug.Log("gradle exit: " + gradle.ExitCode);
					}
					return gradle.ExitCode == 0;
				}
				try
				{
					gradle.Kill();
					Debug.Log("gradle timeout:" + gradle.ExitCode);
					return gradle.ExitCode == 0;
				}
				catch (Exception ex){
					Debug.Log("gradle error:" + ex.Message);
					return false;
				}
			}
		}
		Debug.Log("gradle error: location " + androidLocation + " not found");
		return false;
	}

	public static bool RunSocketIOServer() {
		System.Diagnostics.Process node = new System.Diagnostics.Process();
		node.StartInfo.FileName = "node";
		node.StartInfo.Arguments = "index.js";
		node.StartInfo.WorkingDirectory = System.IO.Path.GetFullPath("./Assets/WebRTCForUnity/Example/SocketIO/Server~");
		return node.Start ();

	}

	public static bool InstallServerDependencies() {
		System.Diagnostics.Process node = new System.Diagnostics.Process();
		node.StartInfo.FileName = "npm";
		node.StartInfo.Arguments = "install";
		node.StartInfo.WorkingDirectory = System.IO.Path.GetFullPath("./Assets/WebRTCForUnity/Example/SocketIO/Server~");
		return node.Start ();
	}

}
