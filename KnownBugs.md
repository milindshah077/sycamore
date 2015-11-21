# Features not yet implemented #

Here follows a list of features that are planned but not yet implemented in Sycamore. If you have a feature that you would like to see in Sycamore, please use the <a href='https://groups.google.com/forum/#!categories/sycamore-discussion-group/feature-request'>Feature Request</a> section in our discussion group.

  * ~~The Agreement plugin is not yet supported. All coordinates in the system now are just global coordinates (there is full agreement on axes between robots). The support for this feature will be implemented soon.~~ **ADDED** in build 05/09/2013
  * ~~The Measure plugin does not write its measured values anywhere.~~ **ADDED** in build 02/12/2013
  * ~~The reporting System and plotting support are not implemented yet.~~ **REMOVED** they will never be
  * The support  for scheduled simulation is not available yet.
  * ~~The visualizer is not available yet.~~ **ADDED** in build 02/12/2013
  * The visibility graph is not yet drawn
  * The visual support elements are not implemented yet
  * ~~Save project / load project are still not supported~~ **ADDED** in build 02/12/2013
  * ~~A preferences pane is missing~~ **ADDED** in build 05/09/2013

# Known Bugs #

Here follows a list of known bugs. Please, feel free to let us know if you find a new bug. Our discussion group has a <a href='https://groups.google.com/forum/#!categories/sycamore-discussion-group/bug-reporting'>Bug Reporting</a> section

  1. 

&lt;minor&gt;

**~~The 2D scene has problems with lighting and with camera placement.~~**FIXED**with build 05/09/2013
  1.**

&lt;serious&gt;

**~~When camera is placed on baricentrum, it will never point to origin again.~~**FIXED**with build 02/12/2013
  1.**

&lt;minor&gt;

**The 2D scene has problems with camera on baricentrum <hint: a new 2D camera is maybe needed. This will solve also the problem 1 >
  1.**

&lt;serious&gt;

**~~The pauses added by the scheduler are not perfectly equal to the actual time that a robot spends without moving. This results in a wrong behavior of the animation control slider.~~**FIXED**with build 05/09/2013
  1.**

&lt;serious&gt;

**~~The majority of the menu items do not work~~**FIXED**with build 05/09/2013
  1.**

&lt;serious&gt;

**~~Dragging in Switch Toggles sometimes does not work~~**FIXED**with build 02/12/2013
  1.**

&lt;serious&gt;

**~~In Follow The Leader algorithm, sometimes none of the robot moves <hint: it is a problem of the AsynchronousSchedulerPriorityQueue>~~**FIXED**with build 05/09/2013
  1.**

&lt;serious&gt;

**~~The animation control slider sometimes generates an Exception~~**FIXED**with build 05/09/2013
  1.**

&lt;serious&gt;

**~~In Near Gathering algorithm, IcePDF does not show the article~~**FIXED**with build 02/12/2013. PDF file is opened externally
  1.**

&lt;grave&gt;

**~~OS X App seems to work only on OS X Mountain Lion, not on other systems~~**SOLVED**with build 05/09/2013. It is a Java 7 problem. Java 6 works everywhere
  1.**

&lt;grave&gt;

**~~Windows installer does not work. To have Sycamore running in Windows, please run the executable jar file~~**SOLVED**with build 05/09/2013. The installer has been replaced with a zip file containing just the exe binary
  1.**

&lt;minor&gt;

**The 2D scene has Z-fighting problems
  1.**

&lt;serious&gt;

