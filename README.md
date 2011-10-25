JS SCREENSHOT
==============

This is a simple widget for the vaadin web-development framework to take client-side screenshots.
It is basically a wrapper for the nice script html2canvas from Niklas von Hertzen <http://html2canvas.hertzen.com>.  

Features
--------

+ Take client-side screenshots without an applet or flash component
+ Transfer of raw image data to server-side
+ Selection of the root widget for the screenshot

Requirements
------------

+ jquery 1.6+ (needs to be included before js-screenshot)

Known Problems
--------------

+ SVG and external images are not supported (related to origin-clean problem)

Usage
-----

A simple example illustrates the usage of the JS Screenshot component: 


``` java
import com.vaadin.Application;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;

import org.netomi.vaadin.screenshot.Screenshot;
import org.netomi.vaadin.screenshot.Screenshot.ScreenshotListener;


public class SimpleScreenshotDemo extends Application, implements ScreenshotListener {
  @Override
  public void init() {
    final Window mainWindow = new Window("Simple Screenshot Demo");

    setMainWindow(mainWindow);

    final Screenshot screenshot = new Screenshot();
    screenshot.addListener(this);

    mainWindow.addComponent(screenshot);

    mainWindow.addComponent(new Label("A Label"));
    mainWindow.addComponent(new NativeButton("A Native button"));
    
    Button button = new Button("Make Screenshot");
    mainWindow.addComponent(button);
    
    button.addListener(new ClickListener() {
      @Override
      public void buttonClick(ClickEvent event) {
        screenshot.makeScreenshot(mainWindow);
      }
    });
  }

  @Override
  public void screenshotReceived(byte[] imageData) {
    // do something with the image data
  }
}
```

Copyright and license
---------------------

Licensed under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain a copy of
the License at
 
http://www.apache.org/licenses/LICENSE-2.0
 
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations under
the License.