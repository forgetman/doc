@file:Suppress("unused")

package test.ui.activity

import android.os.Looper
import android.os.Process
import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import compat.network.NetworkCompat
import compat.network.def.NetworkState
import compat.network.ext.onConnectStateChanged
import compat.signalstrength.SignalStrengthCompat
import compat.telephony.TelephonyCompat
import compat.telephony.ext.onCallStateChanged
import compat.telephony.ext.onSignalStrengthChanged
import compat.telephony.ext.onSimStateChanged
import coroutine.flow.launchIn
import coroutine.flow.mediator.MediatorFlow
import eth.ext.asProgressFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import logger.L
import sugar.ext.runOnSubThread
import test.Bus
import test.databinding.ActivityMainBinding
import test.model.MultiUserRepo
import test.model.TestEnum
import test.model.User
import test.model.UserBuilder
import test.model.UserRepo
import test.network.Api
import test.network.createApi
import test.ui.activity.anim.AnimActivity
import test.ui.viewModel.MainViewModel
import tool.trigger.Trigger
import tool.trigger.constraints.Constraints
import tool.trigger.constraints.NetworkType
import tool.trigger.ext.onTrigger
import tool.trigger.strategy.BackoffStrategy
import vector.MimeType
import vector.app.databinding.activity.DBActivityEx
import vector.app.fitter.Mode
import vector.app.os.dp
import vector.bindingadapter.bind.Bind
import vector.compat.media.MediaCompat
import vector.compat.media.OnConflictStrategy
import vector.datastore.preference.asEnumFlow
import vector.datastore.preference.asObjectFlow
import vector.datastore.preference.putEnum
import vector.datastore.preference.putObject
import vector.ext.mkFile
import vector.ext.safeAppendText
import vector.ext.safeWriteText
import vector.ext.startActivity
import vector.ext.toFile
import vector.proxy.asThreadProxy
import vector.util.DangerousPerm
import vector.util.Dir
import vector.util.EasyPermissions
import java.io.File
import java.text.NumberFormat
import java.util.Locale

class MainActivity : DBActivityEx<MainViewModel>() {

    companion object {
        private const val LOG_TAG = "MainActivity"
    }

    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        val binding = ActivityMainBinding.inflate(inflater)
        binding.owner = this
        return binding
    }

    override fun initializeSystemBar() {
        appBar.mid.addText("测试首页")
    }

    override fun initializeContentView() {
//        testDpConvert()
//        testCoroutines()
//        testMediatorLiveData()
//        testNetwork()
        BusTester().init(this)
//        testService()
//        testMedia()
//        testFile()
//        testScreenBrightness()
//        testPermission()
//        testMultiTask()
//        TelephonyTester().init()
//        DataStoreOwnerTester().init(this)
//        ThreadProxyTester().init(this)
//        L.d(LOG_TAG, "{\"key\":\"value\", \"array\":[1, 2, 3]}")
//        L.json(LOG_TAG, "{\"key\":\"value\", \"array\":[1, 2, 3]}")
    }

    inner class TriggerTester {

        private val trigger = Trigger(this@MainActivity) {
            setTag("www")
            setConstraints(
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            )
            applyStrategy(BackoffStrategy.Builder().tag("www").build())
        }

        fun init() {
            trigger.onTrigger(this@MainActivity) {
                L.www("触发")
                trigger.reset()
            }
            NetworkCompat.onConnectStateChanged(this@MainActivity, this@MainActivity) {
                L.www("network state = $it")
                if (it is NetworkState.Idle) {
                    trigger.launch()
                }
            }

            trigger.launch()
        }
    }

    private fun testFile() {
        EasyPermissions.request(this, DangerousPerm.Storage {
            try {
                val file = File(Dir.External.cache, "test.txt")
                file.mkFile()
                file.safeWriteText("123")
                file.safeAppendText("4567")

                val read = file.readText()
                L.www("testFile read = $read")
            } catch (e: Exception) {
                L.e(e)
            }
        })
    }

    private fun testMultiTask() {
        for (i in 0..100) {
            lifecycleScope.launch {
                val task = async {
                    delay(i * 1000L)
                    L.d("async i = $i")
                }
                task.await()
            }
        }
    }

    private fun testPermission() {
//        EasyPermissions.request(this, DangerousPerm.Camera {
//            L.www("Camera result = $it")
//        })
        EasyPermissions.request(
            this,
            DangerousPerm.Camera {
                L.www("权限 camera ok")
            },
            DangerousPerm.Calendar(),
            DangerousPerm.Contacts(),
            DangerousPerm.Storage(),
            DangerousPerm.Phone(),
            DangerousPerm.Location(),
            DangerousPerm.Microphone(),
            DangerousPerm.Bluetooth {
                L.www("权限 蓝牙 = $it")
            }
        )
    }

    private fun testScreenBrightness() {
//        L.www("system brightness = " + Screen.Brightness.System.getValue())
//        L.www("window brightness = " + Screen.Brightness.Window.getValue(this.window))
//        Screen.Brightness.Window.setValue(window, 78)

//        Screen.Brightness.System.register(this) {
//            L.www("onChanged = $it")
//        }
    }

    private fun testMedia() {
        EasyPermissions.request(this, DangerousPerm.Storage {
            val oldData = MediaCompat.Download.getData(this, "ddname", "joypods", MimeType.Text.Txt)
            val read = oldData?.uri?.toFile()?.readText()
            L.www("read file oldData = $read")

            val oldData2 = MediaCompat.Download.getData(this, "1111", "payme", MimeType.Text.Txt)
            val read2 = oldData2?.uri?.toFile()?.readText()
            L.www("read file oldData2 = $read2")

            val content = System.currentTimeMillis().toString().takeLast(8)
            val save = MediaCompat.Download.save(
                this,
                "ddname",
                "joypods",
                content,
                MimeType.Text.Txt,
                OnConflictStrategy.REPLACE
            )
            L.www("save result = $save, content = $content")
//
//            val data = MediaCompat.Download.getData("1111", "payme", MimeType.Text.Txt)
//            L.www("data = $data")
//            data?.let {
//                val file = it.uri.toFile()
//                L.www("read file new data = ${file?.readText()}")
//            }

//            val count = MediaCompat.Download.deleteInPublicFolder(
//                "payme",
//                MatchRule.Concat(
//                    MatchRule.Contains("txt"),
//                    MatchRule.StartsWith("1")
//                )
//            )
//            L.www("delete = $count")

//            val delete =
//                MediaCompat.Download.delete("1111", "payme", MimeType.Text.Txt)
//            L.www("delete = $delete")
        })
//        val a = MediaCompat.Image.getDataInAlbum("test2/", true)
//        L.www("data count = ${a.size}")
//        a.forEach {
//            L.www("image name = " + it.relativePath   + it.displayName)
//        }
//        val d = Res.getDrawable(this@MainActivity, R.drawable.read_bg0)
//        val b = d?.toBitmap()
//
//        RomPermission.checkAndRequest(
//            this,
//            arrayOf(Permission((Manifest.permission.WRITE_EXTERNAL_STORAGE)))
//        ) { ps ->
//            for (i in 0 until 2) {
//                // 添加
//                val result = b?.saveToAlbum(
//                    secondaryPath = "test2",
//                    displayName = "aaa",
//                    onConflict = OnConflictStrategy.DEFAULT
//                )
//                L.www("save result $i = $result")
//
//                val result2 = b?.saveToAlbum(
//                    secondaryPath = "test3",
//                    displayName = "bbb",
//                    onConflict = OnConflictStrategy.DEFAULT
//                )
//                L.www("save result2 $i = $result2")
//
//                // 删除
////                        val a = MediaCompat.Image.deleteInAlbum(null, "bbb")
////                        L.www("delete result = $a")
//            }
//        }

//        val count = MediaCompat.Image.deleteInAlbum(null, MatchRule.NoRule, true)
//        L.www("fuzzy delete count = $count")
    }

    private fun testService() {
//        this.startService(Intent(this, TestServ::class.java))
//        TestServCreator.create(1234).start(this, 2)

//        TestServ2Creator.create(1111).start(this, 1)
//        TestServ2Creator.create(2222).start(this, 1)
//        TestServ2Creator.create(3333).start(this, 1)

//        TestServCommonCreator.create(1234).start(this)
//        TestServCommonCreator.create(1234).startForeground(this)
    }

    class BusTester {

        fun init(owner: LifecycleOwner) {
            owner.lifecycleScope.launch {
                Bus.get().send(1, 100)
                Bus.get().send(2, 100)
            }

            Bus.get().with(owner).onValue<Int>(1) {
                L.d(LOG_TAG, "收到 value 1 = $it")
            }
            Bus.get().with(owner).onStickyValue<Int>(1) {
                L.d(LOG_TAG, "收到 sticky value 1 = $it")
            }

            Bus.get().with(owner).onMessage(2) {
                L.d(LOG_TAG, "收到 message 2")
            }
            Bus.get().with(owner).onStickyMessage(2) {
                L.d(LOG_TAG, "收到 sticky message 2")
            }

            owner.lifecycleScope.launch {
                delay(1000)
                Bus.get().send(1, 101)
                Bus.get().send(2, 101)
            }
        }
    }

    private fun testNetwork() {
        fun calcSpeed(lastTime: Long, progress: Float, contentLength: Long): String {
            val diff = System.currentTimeMillis() - lastTime
            return if (diff == 0L) {
                "0M/S"
            } else {
                val speed = contentLength * 1000f / diff * (progress / 100)
                // speed单位是byte/s, 转为M/s
                val speedInM = NumberFormat.getNumberInstance(Locale.SIMPLIFIED_CHINESE)
                    .apply { maximumFractionDigits = 2 }
                    .format((speed / 1024f / 1024f))
                "${speedInM}M/S"
            }
        }

        var startTime = 0L
        var lastTime = 0L
        var lastProgress = 0f
        createApi<Api>()
//            .downloadApk("https://github.com/CarGuo/GSYVideoPlayer/releases/download/9.0.0-release-jitpack/app-release.apk")
            .downloadApk("https://api.miaobue.cn/apk.zip")
            .asProgressFlow()
            .onProgress {
                val speed = calcSpeed(lastTime, it.progress - lastProgress, it.contentLength)
                lastTime = System.currentTimeMillis()
                lastProgress = it.progress
                L.www("下载进度 = ${it.progress}, 速度 = $speed")
            }.onStart {
                startTime = System.currentTimeMillis()
                lastTime = startTime
                L.www("开始下载")
            }.onEach {
                val diff = System.currentTimeMillis() - startTime
                val speed = it.contentLength * 1000f / diff
                // speed单位是byte/s, 转为M/s
                val speedInM = NumberFormat.getNumberInstance(Locale.SIMPLIFIED_CHINESE)
                    .apply { maximumFractionDigits = 2 }
                    .format((speed / 1024f / 1024f))
                "${speedInM}M/S"
                L.www("下载完成, 速度 = $speedInM")
            }.onCompletion {

            }.catch { e ->
                L.e("www", "testNetwork", e)
            }.flowOn(Dispatchers.Main).launchIn(this)

//        Net.create<Api>()
//            .toJson(listOf("111", "222", "333"))
//            .flowOn(Dispatchers.IO)
//            .catch { }
//            .launch(this) {
//            }
    }

    private fun testCoroutines() {
//        coroutinesAsync()
//        coroutinesLaunch()
    }

    inner class TestMediatorFlow {
        private val flow1 = MutableStateFlow(false)
        private val flow2 = MutableStateFlow(false)

        private val mediator = MediatorFlow(flow1, flow2) { accumulator, value ->
            accumulator and value
        }.stateIn(lifecycleScope, SharingStarted.Eagerly, false)

        fun init() {

        }
    }

    private fun coroutinesAsync() {
        lifecycleScope.launch(Dispatchers.IO) {
            repeat(100_000) {
                val a = async {
                    delay(1000L)
                    println("run blocking $it")
                    it
                }
                val b = a.await()
                println("run blocking2 $b")
                if (b >= 1000) a.cancel()
            }
        }
    }

    private fun coroutinesLaunch() {
        val a = lifecycleScope.launch(Dispatchers.IO) {
            repeat(100_000) {
                launch {
                    delay(1000L)
                    println("run blocking $it")
                }
            }
        }
        a.cancel()
    }

    private fun testDpConvert() {
        doOnLayout {
            L.groupBy(
                "dp this = " + 100.dp.toPx(this),
                "dp default = " + 100.dp.toPx(Mode.DEFAULT),
                "dp width = " + 100.dp.toPx(Mode.WIDTH),
                "dp height = " + 100.dp.toPx(Mode.HEIGHT),
                "dp FULL_SCREEN = " + 100.dp.toPx(Mode.FULL_SCREEN)
            ).d()
        }

        val inflater1 = LayoutInflater.from(this)
        val inflater2 = LayoutInflater.from(this)
        val inflater3 = LayoutInflater.from(this).cloneInContext(this)
        val inflater4 = LayoutInflater.from(this).cloneInContext(this)
        L.groupBy(
            "inflater1 = $inflater1",
            "inflater2 = $inflater2",
            "inflater3 = $inflater3",
            "inflater4 = $inflater4"
        ).d()
    }

    override fun onDestroy() {
        super.onDestroy()

        Bus.close()
    }

    val onWebClick = Bind.OnClick {
        startActivity<WebActivity>()
    }

    val onLottieClick = Bind.OnClick {
        startActivity<LottieActivity>()
    }

    val onPageClick = Bind.OnClick {
        startActivity<PageActivity>()
    }

    val onCustomAnimClick = Bind.OnClick {
        startActivity<AnimActivity>()
    }

    val onDynamicFragClick = Bind.OnClick {
        startActivity<FragStateActivity>()
    }

    val onDiffUtilClick = Bind.OnClick {
        startActivity<DiffActivity>()
    }

    private val dialog by lazy { CommonDialog(this) }
    val onDialogClick = Bind.OnClick {
        dialog.setCanceledOnTouchOutside(false)
        dialog.setOnCancelListener {
            L.www("common dialog cancel")
        }
        dialog.setOnDismissListener {
            L.www("common dialog dismiss")
        }
        dialog.setOnShowListener {
            L.www("common dialog show")
        }
        dialog.show()
    }

    val onFormClick = Bind.OnClick {
        startActivity<FormActivity>()
    }

    inner class TelephonyTester {

        fun init() {
            TelephonyCompat.onSignalStrengthChanged(this@MainActivity, this@MainActivity) {
                L.www(
                    "signalStrength = ${
                        SignalStrengthCompat.getSignalLevel(
                            this@MainActivity,
                            it
                        )
                    }"
                )
            }

            TelephonyCompat.onSimStateChanged(this@MainActivity, this@MainActivity) {
                L.www("simCard = $it")
            }

            TelephonyCompat.onCallStateChanged(this@MainActivity, this@MainActivity) {
                L.www("callState = $it")
            }
        }
    }

    class DataStoreOwnerTester {
        private val userRepo = UserRepo()
        private val multiUserRepo = MultiUserRepo()

        fun init(owner: LifecycleOwner) {
            userRepo.name.asFlow().onEach { L.www("name = $it") }.launchIn(owner)
            userRepo.syncName.asFlow().onEach { L.www("sync name = $it") }.launchIn(owner)
            userRepo.testEnum.asEnumFlow<TestEnum>().onEach { L.www("enum name = $it") }
                .launchIn(owner)
            userRepo.user.asObjectFlow<User>().onEach { L.www("object name = $it") }
                .launchIn(owner)
            owner.lifecycleScope.launch {
                userRepo.name.put("测试名字")
                userRepo.testEnum.putEnum(TestEnum.B)
                UserBuilder().name("测试用户").age(18).build().let {
                    userRepo.user.putObject(it)
                }
            }
            userRepo.syncName.put("sync 测试名字")


            multiUserRepo.name.asFlow().onEach {
                L.www("multi name = $it, pid = ${Process.myPid()}")
                L.d(LOG_TAG, "init, multi name = $it, pid = ${Process.myPid()}")
            }.launchIn(owner)
            multiUserRepo.testEnum.asEnumFlow<TestEnum>().onEach {
                L.www("multi enum name = $it, pid = ${Process.myPid()}")
            }.launchIn(owner)
            multiUserRepo.user.asObjectFlow<User>().onEach { L.www("multi object name = $it") }
                .launchIn(owner)

            owner.lifecycleScope.launch {
                multiUserRepo.name.put("多进程测试名字")
                multiUserRepo.testEnum.putEnum(TestEnum.C)
                multiUserRepo.user.putObject(User("测试用户", 19))
            }
        }
    }

    class ThreadProxyTester {
        interface TestProxy {
            fun test()
            fun test2(): Boolean
            fun test3(int: Int): Int

            interface Callback {
                fun onResult(result: Boolean)
            }

            fun callback(callback: Callback)
        }

        class Proxy : TestProxy {
            override fun test() {
                L.www("test on " + Thread.currentThread().name)
            }

            override fun test2(): Boolean {
                L.www("test2 on " + Thread.currentThread().name)
                return true
            }

            override fun test3(int: Int): Int {
                L.www("test3: int $int on " + Thread.currentThread().name)
                return 0
            }

            override fun callback(callback: TestProxy.Callback) {
                L.www("callback on " + Thread.currentThread().name)
                callback.onResult(true)
            }
        }

        fun init(owner: LifecycleOwner) {
            //        val testProxy = Proxy().asThreadProxy<TestProxy>(Dispatchers.IO)
            val testProxy = Proxy().asThreadProxy<TestProxy>(Looper.getMainLooper())
            testProxy.test()
            runOnSubThread(owner) {
                testProxy.test()
                val result = testProxy.test2()
                L.www("test2 result = $result on " + Thread.currentThread().name)
                testProxy.test3(111)
                testProxy.callback(object : TestProxy.Callback {
                    override fun onResult(result: Boolean) {
                        L.www("callback result = $result on " + Thread.currentThread().name)
                    }
                })
            }

            testProxy.test3(444)
            testProxy.test3(555)
            testProxy.test3(666)
        }
    }
}
