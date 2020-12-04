import java.util.ArrayList;

import org.newdawn.slick.Graphics;

public class MultiValueOption extends MenuOption {

	ArrayList<String> options;
	int curr;
	
	public MultiValueOption(int x, int y, String label, ArrayList<String> options) {
		super(x, y, label);
		this.options = options;
		curr = 0;
	}
	
	public void render(Graphics g, boolean selected) {
		super.render(g, selected);
		int width = g.getFont().getWidth(label);
		g.drawString(options.get(curr), x + width + 5, y);
	}
	
	public void next() {
		curr++;
		if(curr > options.size() - 1) curr = 0;
	}
	
	public void prev() {
		curr--;
		if(curr < 0) curr = options.size() - 1;
	}
	
	public String getSelection() {
		return options.get(curr);
	}
	
}
