package org.netomi.vaadin.screenshot.gwt.client;

/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;

public class VScreenshot extends Widget implements Paintable {
  
  public static final String SCREENSHOT_EVENT = "makeScreenshot";
  public static final String TRACING_ATTR = "tracing";
  public static final String IMAGE_EVENT = "image";
  
  private boolean tracing = false;
  
  private ApplicationConnection client;
  
  public VScreenshot() {
    setElement(Document.get().createDivElement());
    if (BrowserInfo.get().isIE6()) {
      getElement().getStyle().setProperty("overflow", "hidden");
      getElement().getStyle().setProperty("height", "0");
    }
  }
  
  @Override
  public void updateFromUIDL(final UIDL uidl, final ApplicationConnection client) {
    if (client.updateComponent(this, uidl, true)) {
      return;
    }
    
    this.client = client;
    
    if (uidl.hasAttribute(TRACING_ATTR)) {
      tracing = uidl.getBooleanAttribute(TRACING_ATTR);
    }
      
    if (uidl.hasAttribute(SCREENSHOT_EVENT)) {
      Paintable target = uidl.getPaintableAttribute(SCREENSHOT_EVENT, client);
      Element el = client.getElementByPid(client.getPid(target));
      makeScreenshot(el, tracing);
    }
  }
  
  public native String makeScreenshot(com.google.gwt.dom.client.Element element, boolean tracing) /*-{
    var that = this;
    var options = {
      showMessage: tracing,
      received: function(data) {
        that.@org.netomi.vaadin.screenshot.gwt.client.VScreenshot::receivedData(Ljava/lang/String;)(data);
      }
    };
    $wnd.screenshot(element, options);
  }-*/;
  
  protected void receivedData(String data) {
    client.updateVariable(client.getPid(getElement()), IMAGE_EVENT, data, false);
    client.sendPendingVariableChanges();
  }
}
