Zmask
=====

An open source image masker/unmasker written in Java.

History and Goal
----------------
Seeing patterns and solving problems is fun. So, when confronted with Gmask it
did not take very long to get stuck solving all the problems that are out
there. Yes, most are porn, but it feels as if you could get that without
wrangling a picture for a couple of minutes. Plus, it need not be porn.

However, Gmask is a closed-source 32-bit freeware application that has some
large problems with color distortion when you for example do a XOR mask and
save the image. There are some additional closed source implementations, such
as FLmask and Easymask, that seems to be defunct in one way or another.

There is also a project called [jmask](http://code.google.com/p/jmask/) that at
first glance seems to be pretty much the same as zmask: An open source image
masker/unmasker written in Java. Sounded great right there, but when a look at
the code mostly created confusion and questions about source code
maintainability, some scepticism started to grow. When it was discovered that
the masks are still not open source, Zmask was born.

A summary of other applications, plus a nice and graphical guide is available
at <http://gmask.awardspace.info/>.

Zmask was first developed in NetBeans to simplify all GUI cruft. When the
author realized that NetBeans was just about as good as crap on a stick at
this, and that the NetBeans layout manager works best with things that never
will hit production, all NetBeans code was stripped out and is now developed
and maintained using vim.

The goal is to foremost create a open source version that can do everything
that Gmask can, with the CP mask not especially prioritized, and with a better
image (foremost palette) handling. When this goal is reached, it is time to
look into other goodies such as a scripting interface for masking/unmasking
automation, tree-based undo, the CP mask and perhaps a solver-helper that
expands the different mask options in a tree-view and let the operator select
the next step in the expansion.

Developer Information
---------------------
Source is managed through Git.

To do a read-only clone, use the git protocol:

    git clone git://github.com/zqad/zmask.git

To view the source in your browser, just visit the homepage on github:
<https://github.com/zqad/zmask>

If you want to discuss anything, drop a mail on the zmask-devel AT
lists.sourceforge.net mailing list.
