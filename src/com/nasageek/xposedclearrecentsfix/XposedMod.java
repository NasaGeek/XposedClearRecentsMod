package com.nasageek.xposedclearrecentsfix;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import android.content.res.XResources;
import android.graphics.drawable.Drawable;
import android.view.View;
import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class XposedMod implements IXposedHookLoadPackage, IXposedHookInitPackageResources {
	
	public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
		if(lpparam.packageName.equals("com.android.systemui")) {
			
			//before we set the navbar listeners, we repurpose the "recents" listener so it won't clear recents
			XposedHelpers.findAndHookMethod("com.android.systemui.statusbar.phone.PhoneStatusBar", lpparam.classLoader, 
					"prepareNavigationBarView", new XC_MethodHook() {
				protected void beforeHookedMethod(final MethodHookParam param) throws Throwable {
					final View.OnClickListener mRecentsClickListener = new View.OnClickListener() {
				        @Override
				        public void onClick(View v) {
				        	try {
				        		callMethod(param.thisObject, "awakenDreams");
				        		callMethod(param.thisObject, "toggleRecentApps");
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							} 			            
				        }
				    };
					XposedHelpers.setObjectField(param.thisObject, "mRecentsClickListener", mRecentsClickListener);
				}
			});
		} 
	}

	@Override
	public void handleInitPackageResources(final InitPackageResourcesParam resparam) throws Throwable {
		if (!resparam.packageName.equals("com.android.systemui"))
			return;
		
		//sub out clear recents icon with recents icon
		resparam.res.setReplacement("com.android.systemui" , "drawable" , "ic_sysbar_recent_clear", new XResources.DrawableLoader() {
			
			@Override
			public Drawable newDrawable(XResources res, int id) throws Throwable {	
				int drawableId = resparam.res.getIdentifier("ic_sysbar_recent", "drawable", "com.android.systemui");
				return resparam.res.getDrawable(drawableId);
			}
		});
		//sub out clear recents icon with recents icon
		resparam.res.setReplacement("com.android.systemui" , "drawable" , "ic_sysbar_recent_clear_land", new XResources.DrawableLoader() {
			
			@Override
			public Drawable newDrawable(XResources res, int id) throws Throwable {	
				int drawableId = resparam.res.getIdentifier("ic_sysbar_recent_land", "drawable", "com.android.systemui");
				return resparam.res.getDrawable(drawableId);
			}
		});
		
	}
}
