Shader "WebRTC/VideoShader"
{
	SubShader
	{
         Pass 
         {
 			Name "FlipV_OESExternal_To_RGBA"
			ZTest Always Cull Off ZWrite Off Blend Off

			GLSLPROGRAM       

			#extension GL_OES_EGL_image_external : require
			#pragma glsl_es2

			#ifdef VERTEX

			varying vec2 textureCoord;

			void main()
			{
				gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
				textureCoord = vec2(gl_MultiTexCoord0.x, 1.0 - gl_MultiTexCoord0.y);
			}

			#endif

			#ifdef FRAGMENT

			vec4 AdjustForColorSpace(vec4 color)
			{
			#ifdef UNITY_COLORSPACE_GAMMA
				return color;
			#else
				// Approximate version from http://chilliant.blogspot.com.au/2012/08/srgb-approximations-for-hlsl.html?m=1
				vec3 sRGB = color.rgb;
				return vec4(sRGB * (sRGB * (sRGB * 0.305306011 + 0.682171111) + 0.012522878), color.a);
			#endif
			}

			varying vec2 textureCoord;
			uniform samplerExternalOES _MainTex;

			void main()
			{
				gl_FragColor = AdjustForColorSpace(textureExternal(_MainTex, textureCoord));
			}

			#endif

			ENDGLSL
         }
	}

	FallBack Off
}
 