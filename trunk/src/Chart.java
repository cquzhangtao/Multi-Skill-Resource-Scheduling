import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JComponent;

import model.Activity;
import model.Model;
import model.Resource;

public class Chart extends JComponent implements MouseListener, MouseMotionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<Activity> activities;
	private Map<String, Resource> resources;
	private Model model;
	private double xscale = 1;
	private double yscale = 1;
	private int x0 = 50;
	private int y0 = 150;

	private boolean dragging;

	public Chart(Map<String, Resource> resources, List<Activity> activities) {
		super();
		this.activities = activities;
		this.resources = resources;
	}

	public Chart(Model model) {
		super();

		this.model = model;
		this.activities = model.getActivities();
		this.resources = model.getResources();

		addMouseListener(this);
		addMouseMotionListener(this);

	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.white);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());

		g.setColor(Color.black);
		g.drawLine(x0, y0 - 20, this.getWidth(), y0 - 20);
		g.drawLine(x0, y0, x0, this.getHeight());
		Map<Resource, List<TimeSlice>> slices = generate();
		long xmax = 0;
		int ymax=0;
		for (Resource res : slices.keySet()) {
			ymax+=res.getAmount();
			List<TimeSlice> slice = slices.get(res);
			for (int i = 0; i < slice.size(); i++) {
				TimeSlice ts = slice.get(i);
				if (xmax < ts.getEnd()) {
					xmax = ts.getEnd();
				}
			}
		}
		
		ymax+=1*slices.size();
		
		//yscale=1.0*(getHeight()-200)/(slices.size()*30);
		yscale=1.0*(getHeight()-200)/ymax;
		xscale=1.0*(getWidth()-200)/xmax;
		
		g.setColor(Color.black);

		int unit =Math.max(1, (int) (xmax / 20));
		double sunit = unit * xscale;
		int xunitNum=(int) ((getWidth()-x0)/sunit);
		for (int i = 0; i <= xunitNum; i++) {
			int x = (int) (x0 + i * sunit);
			g.drawLine(x, y0 - 20 - 5, x, y0 - 20 + 5);
			g.drawString(String.valueOf(unit * i), x, y0 - 20 - 5);
		}
		g.setColor(new Color(0xf2,0xf2,0xf2));

		for (int i = 1; i <= xunitNum; i++) {
			int x = (int) (x0 + i * sunit);
			g.drawLine(x, y0 - 20 + 15, x, getHeight());

		}
		
		int cury=0;

		for (Resource res : slices.keySet()) {

			double yy = cury;
			cury+=res.getAmount()+1;
			double limit = yy + res.getAmount();

			g.setColor(Color.black);
			g.drawString(res.getId(), x0 - 30, (int) (y0 + (yy + limit) / 2 * yscale));
			
			g.drawString(String.valueOf(0), x0 - 15, (int) (y0 + yy * yscale) + 5);
			g.drawString(String.valueOf(res.getAmount()), x0 - 20, (int) (y0 + limit * yscale));

			List<TimeSlice> slice = slices.get(res);
			g.setColor(Color.red);
			g.drawLine(x0, (int) (y0 + yy * yscale), getWidth(), (int) (y0 + yy * yscale));
			g.drawLine(x0, (int) (y0 + limit * yscale),getWidth(), (int) (y0 + limit * yscale));

			

			for (int i = 0; i < slice.size(); i++) {
				TimeSlice ts = slice.get(i);

				if (ts.getAmount() > 0) {
					int x = x0 + (int) (ts.getStart() * xscale);
					int y = y0 + (int) (yy * yscale);
					int width = (int) ((ts.getEnd() - ts.getStart()) * xscale);
					int height = (int) (ts.getAmount() * yscale);
					
					g.setColor(Color.LIGHT_GRAY);
					g.fillRect(x, y, width, height);
					g.setColor(Color.blue);
					g.drawRect(x, y, width, height);
					g.setColor(Color.BLACK);
					String label = "";
					int idx = 0;
					for (Activity act : ts.getActivities()) {
						// label+=act.getId()+"\r\n";
						g.drawString(act.getId() + "(" + ts.getActAmount(act) + ")", x, y + idx * 10 + 10);
						idx++;
					}

				}

			}

		}

	}

	private Map<Resource, List<TimeSlice>> generate() {
		Map<Resource, Set<Long>> timePoints = new HashMap<Resource, Set<Long>>();
		Map<Resource, List<TimeSlice>> slices = new HashMap<Resource, List<TimeSlice>>();

		for (Activity act : activities) {
			Map<String, Map<String, Integer>> assignment = act.getAssignment();

			for (Map<String, Integer> ress : assignment.values()) {
				for (String res : ress.keySet()) {

					Set<Long> points = timePoints.get(resources.get(res));
					if (points == null) {
						points = new HashSet<Long>();
						timePoints.put(resources.get(res), points);
					}

					points.add(act.getStartTime());

					points.add(act.getEndTime());

				}
			}
		}

		for (Resource res : timePoints.keySet()) {
			Set<Long> points = timePoints.get(res);
			ArrayList<Long> sortedPoints = new ArrayList<Long>(new TreeSet<Long>(points));
			List<TimeSlice> slice = new ArrayList<TimeSlice>();
			slices.put(res, slice);
			for (int i = 0; i < sortedPoints.size() - 1; i++) {
				TimeSlice ts = new TimeSlice(sortedPoints.get(i), sortedPoints.get(i + 1));
				slice.add(ts);
			}
		}

		for (Activity act : activities) {
			Map<String, Map<String, Integer>> assignment = act.getAssignment();

			for (Map<String, Integer> ress : assignment.values()) {
				for (String res : ress.keySet()) {
					int amount = ress.get(res);
					List<TimeSlice> slice = slices.get(resources.get(res));

					for (TimeSlice ts : slice) {

						if (act.getStartTime() <= ts.getStart() && act.getEndTime() >= ts.getEnd()) {
							ts.addActivity(act, amount);
							ts.addAmount(amount);

						}
					}
				}

			}
		}
		return slices;

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (dragging) {
			setCursor(new Cursor(Cursor.MOVE_CURSOR));
			int dX = mousex - e.getX();
			int dY = mousey - e.getY();
			// System.out.println(dX + "," + dY);
			x0 = x0 - dX;
			y0 = y0 - dY;

			repaint();
			mousex = e.getX();
			mousey = e.getY();
		}

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {
			if (e.isControlDown()) {
				xscale -= 0.4;
				yscale -= 0.4;
			} else {
				xscale += 0.4;
				yscale += 0.4;
			}

			repaint();
		}

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	private int mousex = 0;
	private int mousey = 0;

	@Override
	public void mousePressed(MouseEvent e) {
		dragging = true;

		mousex = e.getX();
		mousey = e.getY();

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		dragging = false;
		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

	}
}
