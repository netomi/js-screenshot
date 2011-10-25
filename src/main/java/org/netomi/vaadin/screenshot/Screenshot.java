package org.netomi.vaadin.screenshot;

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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.netomi.vaadin.screenshot.gwt.client.VScreenshot;

import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;

/**
 * A Screenshot component that takes a client-side screenshot using html2canvas and
 * transfers it to the server.
 * 
 * @see https://github.com/niklasvh/html2canvas
 * @author Thomas Neidhart
 */
@com.vaadin.ui.ClientWidget(org.netomi.vaadin.screenshot.gwt.client.VScreenshot.class)
public class Screenshot extends AbstractComponent {

  private static final long serialVersionUID = 1L;

  private final List<ScreenshotListener> listeners = new ArrayList<ScreenshotListener>();

  private Component targetComponent = null;
  private boolean tracing = false;
  
  /**
   * Creates a new {@link Screenshot} instance.
   */
  public Screenshot() {
    super();
  }

  public boolean isTracing() {
    return tracing;
  }

  public void setTracing(boolean tracing) {
    this.tracing = tracing;
  }

  @Override
  public void paintContent(final PaintTarget target) throws PaintException {
    if (targetComponent != null) {
      target.addAttribute(VScreenshot.SCREENSHOT_EVENT, targetComponent);
      target.addAttribute(VScreenshot.TRACING_ATTR, tracing);
      targetComponent = null;
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public void changeVariables(final Object source, @SuppressWarnings("rawtypes") final Map variables) {
    super.changeVariables(source, variables);

    if (variables.containsKey(VScreenshot.IMAGE_EVENT)) {
      // received image
      String data = (String) variables.get(VScreenshot.IMAGE_EVENT);
      int idx = data.indexOf(',');
      byte[] rawData = Base64.decode(data.substring(idx + 1).getBytes());
      fireScreenshotEvents(rawData);
    }
  }

  public void makeScreenshot(final Component target) {
    targetComponent = target;
    requestRepaint();
  }

  private void fireScreenshotEvents(final byte[] rawData) {
    for (final ScreenshotListener listener : listeners) {
      listener.screenshotReceived(rawData);
    }
  }

  /**
   * Add a listener that will be triggered whenever this instance refreshes itself.
   * 
   * @param listener
   *          the listener
   * @return <code>true</code> if the adding was successful. <code>false</code> if the adding was
   *         unsuccessful, or <code>listener</code> is <code>null</code>.
   */
  public boolean addListener(final ScreenshotListener listener) {
    if (listener != null) {
      return listeners.add(listener);
    } else {
      return false;
    }
  }

  /**
   * Removes a {@link ScreenshotListener} from this instance.
   * 
   * @param listener
   *          the listener to be removed.
   * @return <code>true</code> if removal was successful. A <code>false</code> most often means that
   *         <code>listener</code> wasn't added to this instance to begin with.
   */
  public boolean removeListener(final ScreenshotListener listener) {
    return listeners.remove(listener);
  }

  /**
   * The interface for receiving screenshots
   */
  public interface ScreenshotListener extends Serializable {
    public void screenshotReceived(final byte[] imageData);
  }
}
