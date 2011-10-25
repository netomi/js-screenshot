package org.netomi.vaadin.screenshot.demo;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;

import org.netomi.vaadin.screenshot.Screenshot;
import org.netomi.vaadin.screenshot.Screenshot.ScreenshotListener;

import com.vaadin.Application;
import com.vaadin.data.Item;
import com.vaadin.event.Action;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.StreamResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Select;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class SimpleScreenshotDemo extends Application implements ScreenshotListener {

  private Embedded image;

  @Override
  public void init() {
    final Window mainWindow = new Window("Simple EventTimeline Demo");

    setMainWindow(mainWindow);

    final Screenshot screenshot = new Screenshot();
    screenshot.addListener(this);

    mainWindow.addComponent(screenshot);

    mainWindow.addComponent(new Label("A Label"));
    mainWindow.addComponent(new NativeButton("A Native button"));
    final TableStylingExample table = new TableStylingExample();
    mainWindow.addComponent(table);
    
    final HorizontalLayout command = new HorizontalLayout();
    
    Button button = new Button("Make Screenshot");

    command.addComponent(button);
    
    final Select components = new Select();
    components.addItem("Window");
    components.addItem("Table");
    components.addItem("Button/Select Panel");
    components.setNullSelectionAllowed(false);
    
    command.addComponent(components);
    mainWindow.addComponent(command);

    screenshot.setTracing(false);
    
    button.addListener(new ClickListener() {
      @Override
      public void buttonClick(ClickEvent event) {
        String component = (String) components.getValue();
        Component target = null;
        
        if (component == null) {
          target = mainWindow;
        } else if ("Table".equalsIgnoreCase(component)) {
          target = table;
        } else if ("Window".equalsIgnoreCase(component)) {
          target = mainWindow;
        } else {
          target = command;
        }
        
        screenshot.makeScreenshot(target);
      }
    });

    Panel imagePanel = new Panel();
    imagePanel.setSizeFull();
    ((VerticalLayout) imagePanel.getContent()).setSizeUndefined();
    
    imagePanel.setScrollable(true);
    
    image = new Embedded("Screenshot");
    imagePanel.addComponent(image);

    mainWindow.addComponent(imagePanel);
  }

  @Override
  public void screenshotReceived(byte[] imageData) {
    StreamResource imageresource =
      new StreamResource(new MyImageSource(imageData), "screenshot.png", this);
    image.setSource(imageresource); 

//    File f = new File("screenshot.png");
//    FileOutputStream out = null;
//    try {
//      f.createNewFile();
//      out = new FileOutputStream(f);
//      out.write(imageData);
//      out.flush();
//      out.close();
//    } catch (FileNotFoundException e) {
//      e.printStackTrace();
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
  }

  public class MyImageSource implements StreamResource.StreamSource {
    private byte[] data;

    public MyImageSource(byte[] rawData) {
      data = rawData;
    }

    public InputStream getStream() {
      return new ByteArrayInputStream(data);
    }
  }

  public static class TableStylingExample extends VerticalLayout {

    Table table = new Table();

    HashMap<Object, String> markedRows = new HashMap<Object, String>();
    HashMap<Object, HashSet<Object>> markedCells = new HashMap<Object, HashSet<Object>>();

    static final Action ACTION_RED = new Action("red");
    static final Action ACTION_BLUE = new Action("blue");
    static final Action ACTION_GREEN = new Action("green");
    static final Action ACTION_NONE = new Action("none");
    static final Action[] ACTIONS = new Action[] { ACTION_RED, ACTION_GREEN, ACTION_BLUE,
        ACTION_NONE };

    public TableStylingExample() {
      setSpacing(true);

      addComponent(table);

      // set a style name, so we can style rows and cells
      table.setStyleName("contacts");

      // size
      table.setWidth("100%");
      table.setPageLength(7);

      // connect data source
      table.setContainerDataSource(ExampleUtil.getPersonContainer());

      // Generate the email-link from firstname & lastname
      table.addGeneratedColumn("Email", new Table.ColumnGenerator() {
        public Component generateCell(Table source, Object itemId, Object columnId) {
          Item item = table.getItem(itemId);
          String fn =
            (String) item.getItemProperty(ExampleUtil.PERSON_PROPERTY_FIRSTNAME).getValue();
          String ln =
            (String) item.getItemProperty(ExampleUtil.PERSON_PROPERTY_LASTNAME).getValue();
          String email = fn.toLowerCase() + "." + ln.toLowerCase() + "@example.com";
          // the Link -component:
          Link emailLink = new Link(email, new ExternalResource("mailto:" + email));
          return emailLink;
        }

      });

      // turn on column reordering and collapsing
      table.setColumnReorderingAllowed(true);
      table.setColumnCollapsingAllowed(true);

      // Actions (a.k.a context menu)

      table.addActionHandler(new Action.Handler() {
        public Action[] getActions(Object target, Object sender) {
          return ACTIONS;
        }

        public void handleAction(Action action, Object sender, Object target) {
          markedRows.remove(target);
          if (!ACTION_NONE.equals(action)) {
            // we're using the cations caption as stylename as well:
            markedRows.put(target, action.getCaption());
          }
          // this causes the CellStyleGenerator to return new styles,
          // but table can't automatically know, we must tell it:
          table.requestRepaint();
        }

      });

      // toggle cell 'marked' styling when double-clicked
      table.addListener(new ItemClickListener() {
        public void itemClick(ItemClickEvent event) {
          if (event.getButton() == ItemClickEvent.BUTTON_RIGHT) {
            // you can handle left/right/middle -mouseclick
          }

          if (event.isDoubleClick()) {
            Object itemId = event.getItemId();
            Object propertyId = event.getPropertyId();
            HashSet<Object> cells = markedCells.get(itemId);
            if (cells == null) {
              cells = new HashSet<Object>();
              markedCells.put(itemId, cells);
            }
            if (cells.contains(propertyId)) {
              // toggle marking off
              cells.remove(propertyId);
            } else {
              // toggle marking on
              cells.add(propertyId);
            }
            // this causes the CellStyleGenerator to return new styles,
            // but table can't automatically know, we must tell it:
            table.requestRepaint();
          }
        }
      });

      // Editing
      // we don't want to update container before pressing 'save':
      table.setWriteThrough(false);
      // edit button
      final Button editButton = new Button("Edit");
      addComponent(editButton);
      editButton.addListener(new Button.ClickListener() {
        public void buttonClick(ClickEvent event) {
          table.setEditable(!table.isEditable());
          editButton.setCaption((table.isEditable() ? "Save" : "Edit"));
        }
      });
      setComponentAlignment(editButton, Alignment.TOP_RIGHT);
    }
  }
}