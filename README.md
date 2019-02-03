# SashComposite

A simple widget you can put 2 controls in. Allows you to resize them by dragging the middle sash.
Specifically it lets you specify a minimum or maximum size for each control. Please be mindful about resizing the entire app to go below the minimum sizes.

I only made it support 2 controls (left and right, or top and bottom), but if you want more, you can nest them.

### Methods

`public SashComposite(Composite parent, int style)`

Takes in the same arguments as a regular Composite, except the style also accepts either `SWT.HORIZONTAL` or `SWT.VERTICAL`.

`public void setFirstMinimumPixels(int pixels)`

Sets the minimum pixels in the first control. This disables the value for maximum pixels in the last control.

`public void setLastMinimumPixels(int pixels)`

Sets the minimum pixels in the last control. This disables the value for maximum pixels in the first control.

`public void setFirstMaximumPixels(int pixels)`

Sets the maximum pixels in the first control. This disables the value for minimum pixels in the last control.

`public void setLastMaximumPixels(int pixels)`

Sets the maximum pixels in the last control. This disables the value for minimum pixels in the first control.

`public void setSashWidth(int pixels)`

Sets the width, in pixels, the sash itself will use. Default is 3 pixels.

`public void setFirstControl(Control control)`

Sets the given control as the first control in the sash. The control can also be a Composite with its own children.

`public void setLastControl(Control control)`

Sets the given control as the last control in the sash.
