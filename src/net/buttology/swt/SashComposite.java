package net.buttology.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Sash;

public class SashComposite extends Composite {
	
	/** Minimum pixels in the first panel to the sash */
	private int minPixelsFirst = 20;
	
	/** Minimum pixels in the last panel from the sash */
	private int minPixelsLast = 20;
	
	/** Maximum pixels in the first panel to the sash */
	private int maxPixelsFirst = -1;
	
	/** Maximum pixels in the last panel from the sash */
	private int maxPixelsLast = -1;
	
	/** Width of sash itself in pixels */
	private int sashWidth = 3;
	
	/** Whether UI has initialized */
	private boolean initialized = false;
	
	/** The first panel */
	private Composite firstPanel;
	
	/** The last panel */
	private Composite lastPanel;
	
	/** If this widget has been initialized with a horizontal sash */
	private boolean horizontalSash;
	
	private FormData fd_sash;
	
	/**
	 * Creates a new instance of this widget. 
	 * @param parent - The parent this widget will belong to.
	 * @param style - The style of this widget (same as Composite styles).
	 */
	public SashComposite(Composite parent, int style)
	{
		super(parent, SWT.NONE);
		horizontalSash = (style & SWT.HORIZONTAL) == SWT.HORIZONTAL;
		style = SWT.SMOOTH;
		if(!horizontalSash) style |= SWT.VERTICAL;
		construct(style);
	}
	
	/**
	 * Set the minimum pixels the first panel can resize to.
	 * @param pixels
	 */
	public void setFirstMinimumPixels(int pixels)
	{
		minPixelsFirst = pixels;
		maxPixelsLast = -1;
	}
	
	/**
	 * Set the minimum pixels the last panel can resize to.
	 * @param pixels
	 */
	public void setLastMinimumPixels(int pixels)
	{
		minPixelsLast = pixels;
		maxPixelsFirst = -1;
	}
	
	/**
	 * Set the minimum pixels the first panel can resize to.
	 * @param pixels
	 */
	public void setFirstMaximumPixels(int pixels)
	{
		maxPixelsFirst = pixels;
		minPixelsLast = -1;
	}
	
	/**
	 * Set the minimum pixels the last panel can resize to.
	 * @param pixels
	 */
	public void setLastMaximumPixels(int pixels)
	{
		maxPixelsLast = pixels;
		minPixelsFirst = -1;
	}
	
	/**
	 * Set the width of the sash, in pixels.
	 * @param pixels
	 */
	public void setSashWidth(int pixels)
	{
		sashWidth = pixels;
	}
	
	/**
	 * Set the widget that occupies the first panel.
	 * @param control - The control widget.
	 */
	public void setFirstControl(Control control)
	{
		control.setLayoutData(null);
		control.setParent(firstPanel);
	}
	
	/**
	 * Set the widget that occupies the last panel.
	 * @param control - The control widget.
	 */
	public void setLastControl(Control control)
	{
		control.setLayoutData(null);
		control.setParent(lastPanel);
	}
	
	/**
	 * Move the sash to this position, or closest if it conflicts with a size restriction.
	 * @param percentage
	 */
	public void setSashPosition(int percentage)
	{
		int halfSash = (int) (sashWidth / 2.0f);
		if(horizontalSash)
		{
			fd_sash.top = new FormAttachment(percentage, -halfSash);
			fd_sash.bottom = new FormAttachment(percentage, halfSash);
		}
		else
		{
			fd_sash.left = new FormAttachment(percentage, -halfSash);
			fd_sash.right = new FormAttachment(percentage, halfSash);
		}
	}
	
	/**
	 * Construct the widget.
	 * @param style
	 */
	private void construct(int style)
	{
		// Components
		Composite composite = this;
		
		Sash sash = new Sash(composite, style);
		sash.setBackground(composite.getDisplay().getSystemColor(SWT.COLOR_GRAY));

		firstPanel = new Composite(composite, SWT.NONE);
		firstPanel.setBackground(composite.getDisplay().getSystemColor(SWT.COLOR_CYAN));
		
		lastPanel = new Composite(composite, SWT.NONE);
		lastPanel.setBackground(composite.getDisplay().getSystemColor(SWT.COLOR_GREEN));
		
		// Layouts
		FormLayout layout = new FormLayout();
		composite.setLayout(layout);
		
		FillLayout leftLayout = new FillLayout();
		firstPanel.setLayout(leftLayout);
		
		FillLayout rightLayout = new FillLayout();
		lastPanel.setLayout(rightLayout);
		
		// Layout data
		int halfSash = (int) (sashWidth / 2.0f);

		FormData fd_firstPanel = getMaximizedLayoutData();
		if(horizontalSash)
		{			
			fd_firstPanel.bottom = new FormAttachment(sash);
		}
		else
		{
			fd_firstPanel.right = new FormAttachment(sash);			
		}
		firstPanel.setLayoutData(fd_firstPanel);
		
		FormData fd_lastPanel = getMaximizedLayoutData();
		if(horizontalSash)
		{
			fd_lastPanel.top = new FormAttachment(sash);			
		}
		else
		{
			fd_lastPanel.left = new FormAttachment(sash);
		}
		lastPanel.setLayoutData(fd_lastPanel);
		
		fd_sash = new FormData();
		if(!horizontalSash)
		{
			fd_sash.top = new FormAttachment(0);
			fd_sash.bottom = new FormAttachment(100);			
			fd_sash.left = new FormAttachment(50, -halfSash);
			fd_sash.right = new FormAttachment(50, halfSash);
		}
		else
		{
			fd_sash.top = new FormAttachment(50, -halfSash);
			fd_sash.bottom = new FormAttachment(50, halfSash);			
			fd_sash.left = new FormAttachment(0);
			fd_sash.right = new FormAttachment(100);
		}
		sash.setLayoutData(fd_sash);
		
		// Move sash
		sash.addListener(SWT.Selection, new Listener()
		{
			@Override
			public void handleEvent(Event e)
			{
				Rectangle sashRect = sash.getBounds();
				Rectangle parentRect = composite.getClientArea();
				
				// Determines the minimum and maximum size in pixels the first control can have.
				int firstControlMax;
				int firstControlMin;
				boolean changed;
								
				if(horizontalSash) // Controls stacked vertically
				{
					firstControlMax = minPixelsLast == -1 ? maxPixelsFirst : parentRect.height - sashRect.height - minPixelsLast;
					firstControlMin = maxPixelsLast == -1 ? minPixelsFirst : parentRect.height - sashRect.height - maxPixelsLast;
					// Clamp the actual sash position
					e.y = clamp(e.y, firstControlMin, firstControlMax);
					changed = e.y != sashRect.y;
				}
				else // Controls stacked horizontally
				{
					firstControlMax = minPixelsLast == -1 ? maxPixelsFirst : parentRect.width - sashRect.width - minPixelsLast;
					firstControlMin = maxPixelsLast == -1 ? minPixelsFirst : parentRect.width - sashRect.width - maxPixelsLast;
					// Clamp the actual sash position
					e.x = Math.max(Math.min(e.x, firstControlMax), minPixelsFirst);
					changed = e.x != sashRect.x;
				}
				
				// Optimize by not performing additional calculations when the widgets have not changed.
				if(changed)
				{
					int percentage;
					int pixels;
					int offset;
					
					if(horizontalSash)
					{
						percentage = (e.y * 100) / parentRect.height;
						pixels = percentage * parentRect.height / 100;
						offset = e.y - pixels;
						fd_sash.top = new FormAttachment(percentage, offset);
						fd_sash.bottom = new FormAttachment(percentage, offset + sashRect.height);
					}
					else
					{
						percentage = (e.x * 100) / parentRect.width;
						pixels = percentage * parentRect.width / 100;
						offset = e.x - pixels;
						fd_sash.left = new FormAttachment(percentage, offset);
						fd_sash.right = new FormAttachment(percentage, offset + sashRect.width);
					}
					
					composite.layout();
				}
			}
		});
		
		// Resize component
		composite.getShell().addListener(SWT.Resize, new Listener()
		{
			@Override
			public void handleEvent(Event e)
			{
				if(!initialized)
				{
					initialized = true;
					return;
				}
				
				Rectangle firstRect = firstPanel.getBounds();
				Rectangle lastRect = lastPanel.getBounds();
				Rectangle sashRect = sash.getBounds();

				if(horizontalSash)
				{
					if(minPixelsFirst != -1 && firstRect.height <= minPixelsFirst)
					{
						// If first section has minimum size
						fd_sash.top = new FormAttachment(0, minPixelsFirst);
						fd_sash.bottom = new FormAttachment(0, minPixelsFirst + sashRect.height);
					}
					else if(maxPixelsLast != -1 && lastRect.height > maxPixelsLast)
					{
						// OR if last section has maximum size
						fd_sash.top = new FormAttachment(100, -maxPixelsLast - sashRect.height);
						fd_sash.bottom = new FormAttachment(100, -maxPixelsLast);
					}
					
					if(minPixelsLast != -1 && lastRect.height <= minPixelsLast)
					{
						// If last section has minimum size
						fd_sash.top = new FormAttachment(100, -minPixelsLast - sashRect.height);
						fd_sash.bottom = new FormAttachment(100, -minPixelsLast);
					}
					else if(maxPixelsFirst != -1 && firstRect.height > maxPixelsFirst)
					{
						// OR if first section has maximum size
						fd_sash.top = new FormAttachment(0, maxPixelsFirst);
						fd_sash.bottom = new FormAttachment(0, maxPixelsFirst + sashRect.height);
					}
				}
				else
				{
					if(minPixelsFirst != -1 && firstRect.width <= minPixelsFirst)
					{
						// If first section has minimum size
						fd_sash.left = new FormAttachment(0, minPixelsFirst);
						fd_sash.right = new FormAttachment(0, minPixelsFirst + sashRect.width);
					}
					else if(maxPixelsLast != -1 && lastRect.width > maxPixelsLast)
					{
						// OR if last section has maximum size
						fd_sash.left = new FormAttachment(100, -maxPixelsLast - sashRect.width);
						fd_sash.right = new FormAttachment(100, -maxPixelsLast);
					}
					
					if(minPixelsLast != -1 && lastRect.width <= minPixelsLast)
					{
						// If last section has minimum size
						fd_sash.left = new FormAttachment(100, -minPixelsLast - sashRect.width);
						fd_sash.right = new FormAttachment(100, -minPixelsLast);
					}
					else if(maxPixelsFirst != -1 && firstRect.width > maxPixelsFirst)
					{
						// OR if first section has maximum size
						fd_sash.left = new FormAttachment(0, maxPixelsFirst);
						fd_sash.right = new FormAttachment(0, maxPixelsFirst + sashRect.width);
					}
				}
				composite.layout();
			}
		});
	}
	
	private int clamp(int value, int minimum, int maximum) {
		return Math.max(Math.min(value, maximum), minimum);
	}

	/**
	 * Get a full-sized FormLayout data object.
	 * @return
	 */
	private FormData getMaximizedLayoutData()
	{
		FormData data = new FormData();
		data.top = new FormAttachment(0);
		data.left = new FormAttachment(0);
		data.right = new FormAttachment(100);
		data.bottom = new FormAttachment(100);
		return data;
	}
}
