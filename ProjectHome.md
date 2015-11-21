WARNING! Beta software. It may have missing or not working features, and in general its correct behavior is not guaranteed in any case.

  * After installing the simulator, you need to place the plug-ins to use in the appropriate folder in the SYCAMORE workspace
  * For a full list of missing features and known bugs, <a href='https://code.google.com/p/sycamore/wiki/KnownBugs'>click here to visit our wiki page</a>
  * For a guide on how to use Sycamore, <a href='https://code.google.com/p/sycamore/wiki/Sycamore'>click here to visit our wiki page</a>
  * For a guide on how to write your own plugin, <a href='https://code.google.com/p/sycamore/wiki/PluginWritingGuide'>click here to visit our wiki page</a>

A Discussion group is available to exchange questions, code snippets and suggestions! <a href='https://groups.google.com/forum/#!forum/sycamore-discussion-group'>click here to access to it</a>

<img src='http://imageshack.com/a/img4/4137/yf8b.png' align='center' width='600'>

Sycamore is a 2D-3D simulation environment for autonomous mobile robots algorithms. With Sycamore it is possible to test an algorithm as well as to check its behavior under several different situations.<br>
Sycamore is a completely modular system, where you can write your own plugin and integrated in an easy way. Sycamore is completely open source and with the available SDK it is possible for everybody to write its own plugin and check with it the behavior of the system.<br>
<br>
A full documentation about Sycamore, as well as a FAQ page will be available soon. If you have questions, you can ask the project owners.<br>
<br>
<b>Note: On Mac OS X Systems, Sycamore does not work with Java 7.</b> This is a problem of the LWJGL library, that is necessary to Sycamore to work. Hopefully an update will be released, but until that moment <b>it is necessary to use Sycamore with Java 6 on Mac OS X.</b> When trying to start it with Java 7, it will crash. This does not apply to Windows and Linux systems.<br>
<br>
<h1>12/08/2014 - New Version Released!</h1>

A new version of the source code has been committed. In this version the engine has been updated to work in synch with <a href='http://virca.hu'>VirCA</a>, a 3D Internet based interactive virtual environment for collaborative manipulation of robots and other hardware or software equipment.<br>
<br>
Note that this ability is currently supported only for Windows.<br>
<br>
<h1>02/12/2013 - New Build Released!</h1>

A new build of Sycamore (ver 20131202) is available. You can download it from the new Downloads page.<br>
This build solves a number of bugs and presents new features:<br>
<ul><li>Tons of minor bugs fixed<br>
</li><li>More plugins available<br>
</li><li>Visualizer implemented<br>
</li><li>Added support for Lights with intensity<br>
</li><li>Visualizer implemented<br>
</li><li>Following with directional visibility problem has been studied and the plugins are available<br>
</li><li>Save and Load projects features implemented<br>
</li><li>More bugs fixed. Please check the <a href='https://code.google.com/p/sycamore/wiki/KnownBugs'>Known bugs wiki page</a> for details.</li></ul>

<h1>15/09/2013 - New Agreements</h1>

The Agreements kit has been updated. The plugins are now the following:<br>
<br>
<ul><li>Total agreement - Local coordinates system is completely equal for all the robots. The origin of axes, orientation, directions and measure unit are exactly the same.<br>
</li><li>Consistent compass - Each robot has its own origin and measure unit, but the cardinal points north, south, east and west (also up and down in 3D) are the same for all the robots.<br>
</li><li>One axis - Each robot has its own origin and measure unit and just 2 of the cardinal points north, south, east and west (also up and down in 3D) are equal. The others can be different from one robot to another.<br>
</li><li>Any axis (2D) - Each robot has its own origin and measure unit and just 2 of the cardinal points north, south, east and west are equal. The others can be different from one robot to another. In addition, the mapping of the axes may vary from one robot to another, so that what is X for a robot can correspond to Y for another robot.<br>
</li><li>Two axes (3D) -  Each robot has its own origin and measure unit and just 4 of the cardinal points north and south, east and west, up and down are equal. The others can be different from one robot to another.<br>
</li><li>Disorientation - Basically the local coordinates system of a robot can be completely different from the one of another robot.</li></ul>

<h1>05/09/2013 - New Build Released!</h1>

A new build of Sycamore (ver 20130905) is available. You can download it from the usual locations.<br>
This build solves a number of bugs and presents new features:<br>
<ul><li>The Agreement plugins are fully supported, now the plugins support is complete. Together with the application, 4 new plugins has been distributed, in order to give the ability of having total agreement and no agreement. <b>The old agreement plugins are not working anymore</b>, it is highly suggested not to use them.<br>
</li><li>Added the support for the visualization of the local coordinate systems<br>
</li><li>Added a global preferences pane<br>
</li><li>Added a new About panel that gives informations about the application, the system and the memory usage.<br>
</li><li>Added a brand new Splash Screen and a better icon<br>
</li><li>Enhanced the behavior of the menu bars. Now The OS X and Windows menus are different, and the majority of the menu items produce the correct behavior when clicked.<br>
</li><li>Added the ability of importing/exporting plugins via menu bar<br>
</li><li>Fixed a bug of the AsynchronousSchedulerPriorityQueue plugin that was generating NullPointerExceptions<br>
</li><li>Fixed a bug of the slider for animation control that was generating NullPointerExceptions. Now it is possible to use that slider just when the animation is paused and not when is played neither stopped. This is because the slider works just if the timelines of the animation are alive, and when pressing the stop button they are destroyed. This will not apply to the coming soon "Visualizer" application, where it will be always possible to use the animation control slider.<br>
</li><li>More bugs fixed. Please check the <a href='https://code.google.com/p/sycamore/wiki/KnownBugs'>Known bugs wiki page</a> for details.