package com.fintech.sst.ui.activity.setting

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.fintech.sst.R
import com.fintech.sst.base.BaseActivity
import com.fintech.sst.helper.PermissionUtil
import com.fintech.sst.helper.closeTime
import com.fintech.sst.helper.lastNoticeTime
import com.fintech.sst.net.Configuration
import com.fintech.sst.net.Constants.KEY_CLOSE_TIME
import com.fintech.sst.ui.activity.aisle.AisleManagerActivity
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : BaseActivity<SettingContract.Presenter>(),SettingContract.View {
    override fun toNotifactionSetting() {
        PermissionUtil.setNotificationListener(this,100)
    }

    override fun toAppDetailActivity() {
        PermissionUtil.toAppDetailActivity(this)
    }

    override fun toLogin() {
        val intent = Intent(this, AisleManagerActivity::class.java)
        intent.putExtra("exit",true)
        startActivity(intent)
    }

    override val presenter: SettingContract.Presenter = SettingPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        val index = Configuration.getUserInfoByKey(KEY_CLOSE_TIME).toIntOrNull()?:1
        tv_time_setting.text = "通道自动关闭时间（${resources.getStringArray(R.array.close_time)[index]}）"

        tv_notice_setting.setOnClickListener { toNotifactionSetting() }
        tv_permission_setting.setOnClickListener { toAppDetailActivity() }
        tv_time_setting.setOnClickListener {
            MaterialDialog(this)
                    .title(text = "设置通道自动关闭的空闲时间")
                    .listItemsSingleChoice(
                            R.array.close_time,
                            initialSelection = Configuration.getUserInfoByKey(KEY_CLOSE_TIME).toIntOrNull()?:1
                    ){ dialog, index, text ->
                        tv_time_setting.text = "通道自动关闭时间（$text）"
                        closeTime = when(index){
                            0 -> 60 * 1000 * 1
                            1 -> 60 * 1000 * 2
                            2 -> 60 * 1000 * 3
                            3 -> 60 * 1000 * 5
                            4 -> 60 * 1000 * 10
                            5 -> Long.MAX_VALUE
                            else -> Long.MAX_VALUE
                        }
                        lastNoticeTime = System.currentTimeMillis()
                        Configuration.putUserInfo(KEY_CLOSE_TIME,index.toString())
                        dialog.dismiss()
                    }
                    .show()
        }
        tv_clear_data.setOnClickListener {
            AlertDialog.Builder(this)
                    .setMessage("是否清除本地数据库？")
                    .setPositiveButton("确定"){dialog, _ ->
                        dialog.dismiss()
                        presenter.cleatLocalDB()
                    }
                    .setNegativeButton("取消"){dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
        }
        tv_exit_account.setOnClickListener {
            AlertDialog.Builder(this)
                    .setMessage("是否确定退出当前账号？")
                    .setPositiveButton("确定"){dialog, _ ->
                        dialog.dismiss()
                        presenter.exitAccount()
                    }
                    .setNegativeButton("取消"){dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
        }
    }
}