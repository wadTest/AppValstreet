package prospec.watsana.appvalstreet;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONObject;

import prospec.watsana.appvalstreet.All.MyAlert;
import prospec.watsana.appvalstreet.Data.SynUser;

public class LoginActivity extends AppCompatActivity {

//    Explicit ประกาศตัวแปร
    private EditText et_email, et_password;
    private Button btn_login;
    private TextView tv_register;
//    truePassString คือ ตัวแปรที่ใช้เก็บค่า password ที่อ่านได้
    private String uerString, passwordString, truePassString;
    private boolean aBoolean = true;

//     แสดงข้อความต้อนรับ
    Dialog myDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        Get Event การรับกิจกรรมจาก ตัวแปร (ช่องกรอกข้อมูล) ผูกความสัมพันธ์
        et_email = (EditText) findViewById(R.id.et_email);
        et_password = (EditText) findViewById(R.id.et_password);
        btn_login = (Button) findViewById(R.id.btn_login);
        tv_register = (TextView) findViewById(R.id.tv_register);

//       เมื่อกดปุ่มนี้จะขึ้นหน้าเมนู ของหน้าถัดไป
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                uerString = et_email.getText().toString().trim();
                passwordString = et_password.getText().toString().trim();

                if (uerString.equals("") || passwordString.equals("")) {
                    MyAlert myAlert = new MyAlert(LoginActivity.this, "มีช่องว่าง", "กรุณากรอกข้อมูลในช่องว่าง");
                    myAlert.myDialog();
                } else {

                    //No Space ดึงค่าจาก server
                    checkUser();
                }
            }//onClick
        });

//        ส่วนของการแสดงรูปภาพ
        myDialog = new Dialog(this);
    }

    public void ShowPopup(View v) {
        TextView txtclose;

        myDialog.setContentView(R.layout.popup_welcome);
        txtclose = (TextView) myDialog.findViewById(R.id.txtclose);
        txtclose.setText("X");

        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();

                //No Space ดึงค่าจาก server
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
//                ไม่ให้ย้อยกลับมาหน้าเก่า กดปุ่มกลับก็จะออกจากหน้าจอเลย
//                finish();
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();

    }//Method

    private void checkUser() {
        try {

            SynUser synUser = new SynUser(LoginActivity.this);
            synUser.execute();
//            อ่านค่ามันออกมา
            String s = synUser.get();
            Log.d("string", "String From Json user_app_crm ==> " + s);
            String nameLogin = "";
            String titleLogin ="";
            JSONArray jsonArray = new JSONArray(s);
//            วบ loop
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
//                ถ้า  user ที่ลูกค้ากรอก มีค่าเท่ากับสิ่งที่อ่านได้จากฐานข้อมูล
                if (uerString.equals(jsonObject.getString("email"))) {
//                    ให้เอาค่าของ aBoolean มีค่า = false
                    aBoolean = false;
                    truePassString = jsonObject.getString("password");
                    titleLogin = jsonObject.getString("title");
                    nameLogin = jsonObject.getString("name");
                    Log.d("check", "titleLogin nameLogin  ==> " + titleLogin + nameLogin );
                }
            }//for

//            ดูค่าของ aBoolean ว่ามีค่า = true หรือเปล่า
            if (aBoolean) {
//                ถ้ามีค่า = true ในกรณีที่ใส่ user ผิด
                MyAlert myAlert = new MyAlert(LoginActivity.this, "ใส่อีเมล์ผิด",
                        " กรุณาตรวจอีเมล์สอบอีกครั้ง");
                myAlert.myDialog();
            } else if (passwordString.equals(truePassString)) {

                SharedPreferences sharedPreferences = getSharedPreferences("Logout", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("titleLogin", titleLogin);
                editor.putString("NameLogin", nameLogin);
                editor.putString("Logout", "false");
                editor.commit();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                intent.putExtra("DATA_USER", editor);
                startActivity(intent);
//                ไม่ให้ย้อยกลับมาหน้าเก่า กดปุ่มกลับก็จะออกจากหน้าจอเลย
                finish();



            } else {
//                ถ้าเกิดว่าผิด
                MyAlert myAlert = new MyAlert(LoginActivity.this, "ใส่รหัสผ่านไม่ถูกต้อง",
                        "กรุณาตรวจสอบรหัสผ่านอีกครั้ง");
//                แล้วเอาค่าคงตัวมา  myDialog
                myAlert.myDialog();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//Method
}//Class Main