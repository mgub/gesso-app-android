package camp.computer.clay.sequencer;

/*
* Defines a simple object to be displayed in a list view.
*
* This serves as "placeholder" representing the data for the view corresponding to an object in the
* object model.
*/

import android.graphics.Color;

import java.util.ArrayList;
import java.util.UUID;

public class ListItem {

    private UUID uuid;

    // TODO: Store the managed Behavior's UUID.
    // TODO: Store reference to the managed Behavior (retrieved via the local cache).

    public String title;

    // for Lights behavior
    public ArrayList<Boolean> lightStates;
    public ArrayList<Integer> lightColors;

    // for I/O behavior
    public ArrayList<Boolean> ioStates;

    // for Message
    public String message;

    // for Wait behavior
    public int time;

    // for Say
    public String phrase;

    // for Complex
    public String summary;
    public ArrayList<ListItem> listItems;

    public static int DEFAULT_TYPE = CustomAdapter.IO_CONTROL_LAYOUT;

    public int type; // Used by the custom BaseAdapter to select the layout for the list_item_type_light.

    public boolean selected = false;

    public boolean hasFocus = false;

    public boolean repeat = false;

    // default constructor
    public ListItem() {
        this("Title", "Subtitle", DEFAULT_TYPE);
    }

    // main constructor
    public ListItem(String title, String subTitle, int type) {
        super();

        // Assign instance UUID to the list item
        this.uuid = UUID.randomUUID();

        // Set parameters
        this.title = title;
        this.message = subTitle;
        this.type = type;

        // Initialize
        this.selected = false;

        // Initialize type
        initializeType();
    }

    private void initializeType() {
        if (this.type == CustomAdapter.LIGHT_CONTROL_LAYOUT) {

            // Initialize light states to false (off)
            lightStates = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                lightStates.add(false);
            }

            // Initialize light color to blue
            lightColors = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                lightColors.add(Color.rgb(0, 0, 255));
            }

        } else if (this.type == CustomAdapter.IO_CONTROL_LAYOUT) {

            // Initialize I/O states to false (off)
            ioStates = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                ioStates.add(false);
            }

        } else if (this.type == CustomAdapter.MESSAGE_CONTROL_LAYOUT) {

            message = "hello";

        } else if (this.type == CustomAdapter.WAIT_CONTROL_LAYOUT) {

            this.time = 250;

        } else if (this.type == CustomAdapter.SAY_CONTROL_LAYOUT) {

            phrase = "oh, that's great";

        } else if (this.type == CustomAdapter.COMPLEX_LAYOUT) {

            listItems = new ArrayList<>();
            summary = "invalid complex layout";

        }
    }

    // String representation
    public String toString() {
        return this.title + " : " + this.message;
    }
}