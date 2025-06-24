package lib.base.widget

import vector.os.SizeF


abstract class WidgetInfoEx {

    val widget4x4 = mutableListOf<SizeF>()
    val widget4x2 = mutableListOf<SizeF>()
    val widget4x1 = mutableListOf<SizeF>()

    init {
        //		EScreenTypeNone,
        //		EType_Default,
        //		// low
        //		EScreen_240x320,
        //		EScreen_240x400,
        //		// medium
        //		EScreen_320x480,
        //		// high
        //		EScreen_480x800,
        //		EScreen_480x854,
        //		EScreen_540x960,
        //		EScreen_600x1024,
        //		// xhigh
        //		EScreen_640x960,
        //		EScreen_720x1184,
        //		EScreen_720x1280,
        //		EScreen_800x1232,
        //		EScreen_800x1205, // Nexus 7 pad
        //		EScreen_800x1280

        // Init 4x2 widget size
        // none
        widget4x2.add(SizeF(1f, 1f))
        // default
        widget4x2.add(SizeF(700f, 400f))
        // low
        widget4x2.add(SizeF(240f, 125f))
        widget4x2.add(SizeF(240f, 160f))
        // medium
        widget4x2.add(SizeF(320f, 194f))
        // high
        widget4x2.add(SizeF(480f, 321f)) // EScreen_480x800
        widget4x2.add(SizeF(480f, 342f))
        widget4x2.add(SizeF(540f, 337f))
        widget4x2.add(SizeF(600f, 320f))
        // xhigh
        widget4x2.add(SizeF(640f, 350f))
        widget4x2.add(SizeF(700f, 400f))
        widget4x2.add(SizeF(700f, 400f)) // EScreen_720x1280
        widget4x2.add(SizeF(800f, 420f)) // EScreen_800x1232
        widget4x2.add(SizeF(800f, 400f)) // EScreen_800x1205
        widget4x2.add(SizeF(800f, 420f)) // EScreen_800x1280


        /**
         * Init 4x4 widget size
         */
        // none
        widget4x4.add(SizeF(1f, 1f))
        // default
        widget4x4.add(SizeF(700f, 840f))
        // low
        widget4x4.add(SizeF(240f, 250f))
        widget4x4.add(SizeF(240f, 320f))
        // medium
        widget4x4.add(SizeF(320f, 388f))
        // high
        widget4x4.add(SizeF(480f, 620f)) // EScreen_480x800
        widget4x4.add(SizeF(480f, 684f))
        widget4x4.add(SizeF(540f, 674f))
        widget4x4.add(SizeF(600f, 640f))
        // xhigh
        widget4x4.add(SizeF(640f, 700f))
        widget4x4.add(SizeF(700f, 800f))
        widget4x4.add(SizeF(700f, 840f)) // EScreen_720x1280
        widget4x4.add(SizeF(800f, 840f)) // EScreen_800x1232
        widget4x4.add(SizeF(800f, 840f)) // EScreen_800x1205
        widget4x4.add(SizeF(800f, 840f)) // EScreen_800x1280


        /**
         * Init 4x1 widget size
         */
        // none
        widget4x1.add(SizeF(1f, 1f))
        // default
        widget4x1.add(SizeF(700f, 200f))
        // low
        widget4x1.add(SizeF(240f, 60f))
        widget4x1.add(SizeF(240f, 75f))
        // medium
        widget4x1.add(SizeF(320f, 98f))
        // high
        widget4x1.add(SizeF(480f, 150f)) // EScreen_480x800
        widget4x1.add(SizeF(480f, 150f))
        widget4x1.add(SizeF(540f, 150f))
        widget4x1.add(SizeF(600f, 150f))
        // xhigh
        widget4x1.add(SizeF(640f, 170f))
        widget4x1.add(SizeF(700f, 200f))
        widget4x1.add(SizeF(700f, 200f)) // EScreen_720x1280
        widget4x1.add(SizeF(800f, 192f)) // EScreen_800x1232
        widget4x1.add(SizeF(800f, 188f)) // EScreen_800x1205
        widget4x1.add(SizeF(800f, 200f)) // EScreen_800x1280
    }
}
