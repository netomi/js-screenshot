package org.netomi.vaadin.screenshot.demo;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;

public final class ExampleUtil {
    public static final Object iso3166_PROPERTY_NAME = "name";
    public static final Object iso3166_PROPERTY_SHORT = "short";
    public static final Object iso3166_PROPERTY_FLAG = "flag";
    public static final Object hw_PROPERTY_NAME = "name";

    public static final Object locale_PROPERTY_LOCALE = "locale";
    public static final Object locale_PROPERTY_NAME = "name";

    public static final Object PERSON_PROPERTY_FIRSTNAME = "First Name";
    public static final Object PERSON_PROPERTY_LASTNAME = "Last Name";
    private static final String[] firstnames = new String[] { "John", "Mary",
            "Joe", "Sarah", "Jeff", "Jane", "Peter", "Marc", "Robert", "Paula",
            "Lenny", "Kenny", "Nathan", "Nicole", "Laura", "Jos", "Josie",
            "Linus" };
    private static final String[] lastnames = new String[] { "Torvalds",
            "Smith", "Adams", "Black", "Wilson", "Richards", "Thompson",
            "McGoff", "Halas", "Jones", "Beck", "Sheridan", "Picard", "Hill",
            "Fielding", "Einstein" };

    public static IndexedContainer getPersonContainer() {
        IndexedContainer contactContainer = new IndexedContainer();
        contactContainer.addContainerProperty(PERSON_PROPERTY_FIRSTNAME,
                String.class, "");
        contactContainer.addContainerProperty(PERSON_PROPERTY_LASTNAME,
                String.class, "");
        for (int i = 0; i < 50;) {
            String fn = firstnames[(int) (Math.random() * firstnames.length)];
            String ln = lastnames[(int) (Math.random() * lastnames.length)];
            String id = fn + ln;
            Item item = contactContainer.addItem(id);
            if (item != null) {
                i++;
                item.getItemProperty(PERSON_PROPERTY_FIRSTNAME).setValue(fn);
                item.getItemProperty(PERSON_PROPERTY_LASTNAME).setValue(ln);
            }
        }
        return contactContainer;
    }
}
