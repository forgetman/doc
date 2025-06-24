//package reader.model
//
//import com.nhaarman.mockitokotlin2.doReturn
//import com.nhaarman.mockitokotlin2.mock
//import live.Live
//import org.junit.Assert
//import org.junit.Before
//import org.junit.Test
//import org.mockito.MockitoAnnotations
//import reader.FontType
//
///**
// * @author yuansui
// * @since 2020/12/29
// */
//class FontDisplayTest {
//
//    @Before
//    fun setup() {
//        MockitoAnnotations.openMocks(this)
//    }
//
//    @Test
//    fun fontTypeValues() {
//        Assert.assertEquals("默认", FontType.DEFAULT.desc)
//        Assert.assertEquals("方正手迹-小可爱简", FontType.FZSJ_OXKAJ.desc)
//        Assert.assertEquals("方正-卡通简", FontType.FZ_KTJ.desc)
//        Assert.assertEquals("羿创-旗黑", FontType.YC_QH.desc)
//
//        Assert.assertEquals(null, FontType.DEFAULT.path)
//        Assert.assertEquals("font/FZSJ_OXKAJ.ttf", FontType.FZSJ_OXKAJ.path)
//        Assert.assertEquals("font/FZ_KTJ.ttf", FontType.FZ_KTJ.path)
//        Assert.assertEquals("font/YC_QH.ttf", FontType.YC_QH.path)
//    }
//
//    @Test
//    fun setValue() {
//        val data = mock<FontDisplay> {
//            on { type } doReturn FontType.DEFAULT
//
//            val liveFalse = mock<Live<Boolean>> {
//                on { value } doReturn true
//            }
//            on { selected } doReturn liveFalse
//        }
//
//        Assert.assertEquals(FontType.DEFAULT, data.type)
//        Assert.assertEquals(true, data.selected.value)
//    }
//}