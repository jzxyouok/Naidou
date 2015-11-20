package com.itspeed.naidou.app.activity;

import android.app.ProgressDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.itspeed.naidou.R;
import com.itspeed.naidou.api.NaidouApi;
import com.itspeed.naidou.api.Response;
import com.itspeed.naidou.app.AppContext;
import com.itspeed.naidou.app.util.CryptoUtil;
import com.itspeed.naidou.app.util.UIHelper;
import com.itspeed.naidou.model.bean.JsonBean.Entity;

import org.kymjs.kjframe.KJActivity;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpConfig;
import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.ui.ViewInject;
import org.kymjs.kjframe.utils.KJLoger;
import org.kymjs.kjframe.utils.PreferenceHelper;
import org.kymjs.kjframe.utils.SystemTool;

import java.util.Map;

/**
 * Created by jafir on 10/15/15.
 * 登录界面，重写onStart方法，目的在于打开界面的时候 有记住密码的功能
 */
public class LoginActivity extends KJActivity {

    public static final String TAG = LoginActivity.class.getSimpleName();
    @BindView(id = R.id.login_username)
    private EditText editName;
    @BindView(id = R.id.login_password)
    private EditText editPassword;
    @BindView(id = R.id.login_in, click = true)
    private ImageView login;
    @BindView(id = R.id.login_scan, click = true)
    private TextView scan;
    @BindView(id = R.id.login_findback, click = true)
    private TextView findback;
    @BindView(id = R.id.login_sign, click = true)
    private ImageView sign;


    private String name;
    private String password;

    private ProgressDialog dialog;

    @Override
    public void setRootView() {
        setContentView(R.layout.aty_login);
        if (!SystemTool.checkNet(aty)) {
            Toast.makeText(aty, "您的网络没有连接", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void initData() {
        super.initData();
        dialog =new ProgressDialog(aty);
        dialog.setMessage("正在登录...");
        dialog.setCanceledOnTouchOutside(false);


    }

    @Override
    protected void onStart() {
        super.onStart();
        name = PreferenceHelper.readString(aty, TAG, "login_account");
        password = PreferenceHelper.readString(aty, TAG, "login_password");
        //解密
        password = CryptoUtil.decrypto(password);
        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(password)){
            editName.setText(name);
            editPassword.setText(password);
        }
    }

    @Override
    public void widgetClick(View v) {

        switch (v.getId()) {
            case R.id.login_findback:
                break;
            case R.id.login_scan:
                UIHelper.showMain(aty);
                aty.finish();
                break;
            case R.id.login_sign:
                UIHelper.showRegister(aty);
                break;
            case R.id.login_in:
                login();
                break;
        }
    }

    /**
     * 检测账号密码是否合适
     * @return
     */
    private boolean login() {
         name = editName.getText().toString().trim();
         password = editPassword.getText().toString().trim();

        if (name.equals("") || name == null || password == null || password.equals("")) {
            Toast.makeText(aty, "用户名或者密码不能为空", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            NaidouApi.login(name, password, new HttpCallBack() {

                @Override
                public void onSuccess(Map<String, String> headers, byte[] t) {
                    super.onSuccess(headers, t);
                    String s = new String(t);
                    KJLoger.debug(s);
                    Entity entity = Response.getEntity(s);
                    ViewInject.toast(entity.getMessage());
                    if (entity.is_success()) {
                        //设置cookie session
                        HttpConfig.sCookie = headers.get("Set-Cookie");
                        //设置用户ID
                        //继续解析data 然后获取到USERID 设置到全局变量去 方便以后使用
                        JSONObject object = JSON.parseObject(entity.getData().toString());
                        int userID = object.getInteger("userId");
                        AppContext.UID = userID;
                        KJLoger.debug("" + HttpConfig.sCookie);
                        //账号密码写入 SP
                        writeToSP();
                        //跳转
                        UIHelper.showMain(aty);
                        ViewInject.toast("登录成功");
                        dialog.dismiss();
                        aty.finish();
                    }
                }

                @Override
                public void onPreStart() {
                    super.onPreStart();
                    dialog.show();
                }

                @Override
                public void onFailure(int errorNo, String strMsg) {
                    super.onFailure(errorNo, strMsg);
                    KJLoger.debug("错误：" + strMsg);
                    dialog.dismiss();
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    KJLoger.debug("onFinish");
                    dialog.dismiss();
                }
            });
            return false;
        }
    }


    private void writeToSP() {
        PreferenceHelper.write(aty, TAG, "login_account", name);
        PreferenceHelper.write(aty, TAG, "login_password", password);
    }


    @Override
    protected void onDestroy() {
        setContentView(R.layout.view_null);
        name = null;
        password = null;
        dialog = null;
        editName = null;
        editPassword = null;
        login = null;
        scan = null;
        findback = null;
        sign = null;
        super.onDestroy();
    }
}
