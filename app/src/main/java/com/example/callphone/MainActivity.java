package com.example.callphone;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.SecureDirectoryStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    Button button,del_button;
    boolean del_image = false;
    Intent intent = new Intent(Intent.ACTION_CALL);
    public ArrayList<String> phone_number_list = new ArrayList<>();
    String user_phone_number = "";
    // 在 MainActivity 中定义一个列表存储添加的 ImageView 的 id
    List<Integer> imageViewIds = new ArrayList<>();






    private int findIndexOfImageViewWithId(LinearLayout linearLayout, int id) { //用于查找image的id
        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            View child = linearLayout.getChildAt(i);
            if (child instanceof ImageView && child.getId() == id) {
                return i; // 返回找到的 ImageView 的索引
            }
        }
        return -1; // 如果没有找到对应的 ImageView
    }

    public void getuser_phonenumber(){
        // 创建 AlertDialog.Builder 对象
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("请输入电话号码");  // 设置对话框标题

// 设置对话框消息和输入框
        final EditText input = new EditText(MainActivity.this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);  // 设置输入类型为数字
        builder.setView(input);

// 设置对话框按钮及点击事件处理
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @SuppressLint("InlinedApi")
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String userInput = input.getText().toString();  // 获取用户输入的文本

                if(userInput.isEmpty()){
                    Toast.makeText(getApplicationContext(), "你输入的内容为空" + userInput, Toast.LENGTH_LONG).show();
                    dialog.cancel();  // 取消对话框
                }
                else {
                    phone_number_list.add(userInput);
                    user_phone_number = userInput;




                    // 检查是否已经获取了权限
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_MEDIA_IMAGES)
                            != PackageManager.PERMISSION_GRANTED) {
                        // 如果没有权限，则请求权限
                        Toast.makeText(MainActivity.this, "未获取权限", Toast.LENGTH_SHORT).show();
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                                100);


                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);

//                        Toast.makeText(MainActivity.this, "ok", Toast.LENGTH_SHORT).show();

                        saveListToFile(MainActivity.this,"phonenumber.txt",phone_number_list);


                    } else {
                        Toast.makeText(MainActivity.this, "已获取权限", Toast.LENGTH_SHORT).show();
                        // 如果已经有权限，则可以执行读取本地图片的操作
                        // 这里可以调用读取本地图片的方法

                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);

//                        Toast.makeText(MainActivity.this, "ok", Toast.LENGTH_SHORT).show();

                        saveListToFile(MainActivity.this,"phonenumber.txt",phone_number_list);

                        Log.d("LLL",String.join(",",readTxtFileFromDirectory(getExternalFilesDir(null) + "/callphone","phonenumber.txt")));

                    }




                }

                // 处理用户输入，例如将其显示到界面上或者进行其他操作
//                Toast.makeText(getApplicationContext(), "你输入的内容是：" + userInput, Toast.LENGTH_LONG).show();

            }
        });

        builder.setNegativeButton("取消", (dialog, which) -> {
            dialog.cancel();  // 取消对话框

        });

// 创建并显示对话框
        AlertDialog dialog = builder.create();
        dialog.show();

    }



    public  void set_image(Bitmap image) {


        ImageView imageView = new ImageView(MainActivity.this);

//                 设置要显示的图片资源
        imageView.setImageBitmap(image);
        LinearLayout linearLayout = findViewById(R.id.linearLayout);

        // 生成一个唯一的 id
        final int[] id = {View.generateViewId()};

        // 设置ImageView的布局参数
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT

        );
        layoutParams.weight = LinearLayout.LayoutParams.WRAP_CONTENT;
        layoutParams.height = 1500;

        int a = 0;
        if (!del_image) {
            // 设置 ImageView 的 id
            imageView.setId(id[0]);

            // 添加图片到LinearLayout
            linearLayout.addView(imageView, layoutParams);
            a = linearLayout.getChildCount();


        }
        Log.d("aaa", String.valueOf(a));

        // 将 id 存储到列表中
        imageViewIds.add(id[0]);

        Log.d("QQQ", String.valueOf(del_button));
        imageView.setOnClickListener(v -> {
            ArrayList<String> l = readTxtFileFromDirectory(getExternalFilesDir(null) + "/callphone", "phonenumber.txt");
            if (del_image) {
//                    int index = findIndexOfImageViewWithId(linearLayout, Integer.parseInt(l.get(id-1)));
//                    // 从 LinearLayout 中移除 ImageView
//                    linearLayout.removeView(imageView);
//                    // 从 imageViewIds 列表中移除对应的 id
//                    imageViewIds.remove(Integer.valueOf(index));
                // 从 LinearLayout 中移除 ImageView
                linearLayout.removeView(imageView);

                // 从 imageViewIds 列表中移除对应的 id
                imageViewIds.remove(Integer.valueOf(id[0]));
                Log.d("lll", String.valueOf(l) + '?' + id[0]);
                l.remove(id[0] - 1);
                phone_number_list = l;
                saveListToFile(MainActivity.this, "phonenumber.txt", phone_number_list);
                Log.d("lll", String.valueOf(phone_number_list) + ',' + id[0]);
                del_image = false;
                Toast.makeText(MainActivity.this, "ImageView clicked with ID: " + id[0], Toast.LENGTH_SHORT).show();
                linearLayout.removeAllViews();
                imageViewIds.clear();





                for (String number:l){
                    // 1. 从本地文件系统读取图片文件并转换为 Bitmap
                    Bitmap bitmap = BitmapFactory.decodeFile(getExternalFilesDir(null) + "/callphone/"+number+".png");

                    // 2. 将 Bitmap 设置到 ImageView 中显示
                    if (bitmap != null) {
                        set_image(bitmap);
                    } else {
                        // 图片加载失败的处理逻辑
                        Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
                    }
                }

                button.setOnClickListener(view -> getuser_phonenumber());




            }
            // 点击事件处理逻辑
//                 //例如，可以在这里处理点击后的操作，比如显示大图、跳转到详细页面等


            else {
                Log.d("idid", String.valueOf(View.generateViewId()));
                callPhone(l.get(id[0] - 1));
                Toast.makeText(MainActivity.this, "ImageView clicked with ID: " + id[0], Toast.LENGTH_SHORT).show();
            }
        });


        //Toast.makeText(MainActivity.this, "id"+imageViewIds.get(id), Toast.LENGTH_SHORT).show();

    }
    public void callPhone(String phoneNumber){  //拨打电话函数
//        Toast.makeText(MainActivity.this, "ok", Toast.LENGTH_SHORT).show();
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            // 如果已经授予了电话权限，直接启动拨号 Intent
            startActivity(intent);
//            Toast.makeText(MainActivity.this, "a", Toast.LENGTH_SHORT).show();
        } else {
            // 如果尚未授予电话权限，则请求权限
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 100);
//            Toast.makeText(MainActivity.this, "b", Toast.LENGTH_SHORT).show();
//            startActivity(intent);
            
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button=findViewById(R.id.button2);
        del_button = findViewById(R.id.button3);

        del_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                del_image = true; //确认删除键被按下
                Toast.makeText(MainActivity.this, "请选中一个图片然后删除", Toast.LENGTH_SHORT).show();
                set_image(null);
            }
        });

        ArrayList<String> l = readTxtFileFromDirectory(getExternalFilesDir(null) + "/callphone","phonenumber.txt");
        phone_number_list = l;




        for (String number:l){
            // 1. 从本地文件系统读取图片文件并转换为 Bitmap
            Bitmap bitmap = BitmapFactory.decodeFile(getExternalFilesDir(null) + "/callphone/"+number+".png");

            // 2. 将 Bitmap 设置到 ImageView 中显示
            if (bitmap != null) {
                set_image(bitmap);
            } else {
                // 图片加载失败的处理逻辑
                Toast.makeText(MainActivity.this, "erro", Toast.LENGTH_SHORT).show();
            }
        }

        button.setOnClickListener(view -> getuser_phonenumber());
    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();

            // 根据 URI 加载图片到 ImageView 或者进行其他处理
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                createFolder();
                // 假设 bmp 是你的 Bitmap 对象
                //设置图片路径
                File picturesDirectory = new File( getExternalFilesDir(null) + "/callphone");
                File file = new File(picturesDirectory, user_phone_number+".PNG");
                FileOutputStream outputStream = new FileOutputStream(file);

                // 将 Bitmap 压缩为 PNG 格式并保存到输出流
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.close();

                set_image(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("eee",""+e);
//                Toast.makeText(MainActivity.this, ""+e, Toast.LENGTH_SHORT).show();

            }
        }
    }



    // 根据 URI 获取图片的路径
    private String getPathFromUri(Uri contentUri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, projection, null, null, null);
        if (cursor == null) {
            return contentUri.getPath(); // Fallback to path if cursor is null
        } else {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            String path = cursor.getString(columnIndex);
            cursor.close();
            return path;
        }
    }


    private void createFolder() {
        File folder;

        String folderPath = getExternalFilesDir(null) + "/callphone";
        File folder2 = new File(folderPath);

        // 检查文件夹是否存在
        if (folder2.exists() && folder2.isDirectory()) {
            Log.d(TAG, "文件夹存在：" + folder2.getAbsolutePath());
        } else {
            Log.d(TAG, "文件夹不存在：" + folder2.getAbsolutePath());


            //创建callphone文件夹
            // 检查外部存储是否可用
            if (isExternalStorageWritable()) {
                // 获取外部存储的文件夹路径
                folder = new File(getExternalFilesDir(null), "callphone");
            } else {
                // 如果外部存储不可用，使用内部存储路径
                folder = new File(getFilesDir(), "callphone");
            }

            // 创建文件夹及其父文件夹（如果不存在）
            boolean success = true;
            if (!folder.exists()) {
                success = folder.mkdirs();
            }

            if (success) {
                // 文件夹创建成功
                Log.d(TAG, "Folder created successfully: " + folder.getAbsolutePath());
            } else {
                // 文件夹创建失败
                Log.e(TAG, "Failed to create folder: " + folder.getAbsolutePath());
            }



        }



    }


    public void saveListToFile(Context context, String fileName, List<String> list) {


        StringBuilder stringBuilder = new StringBuilder();
        for (String item : list) {
            stringBuilder.append(item).append("\n"); // 可以根据需要添加分隔符或其他格式
        }

        String fileContent = stringBuilder.toString();

        File dir = new File(getExternalFilesDir(null) + "/callphone"); // 自定义文件夹名
        if (!dir.exists()) {
            dir.mkdirs(); // 创建文件夹及其父文件夹（如果不存在）
        }

        File file = new File(dir, fileName);
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file, false); // 第二个参数为 false 表示覆盖文件内容
            fileWriter.write(fileContent);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileWriter != null) {
                    fileWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }




    public  ArrayList<String> readTxtFileFromDirectory(String directoryPath, String fileName) {



        ArrayList<String> lines = new ArrayList<>();
        BufferedReader bufferedReader = null;

        try {
            File file = new File(directoryPath, fileName);
            bufferedReader = new BufferedReader(new FileReader(file));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        return lines;
    }


    // 检查外部存储是否可写
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }




}