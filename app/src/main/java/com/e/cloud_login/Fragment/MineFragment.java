package com.e.cloud_login.Fragment;

import android.accounts.Account;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.e.cloud_login.Adapter.ResultListBeanAdapter;
import com.e.cloud_login.Data.FilelistBean;
import com.e.cloud_login.Data.JSON.DownLoadBean;
import com.e.cloud_login.Data.JSON.FileBean;
import com.e.cloud_login.Data.JSON.FileListJson;
import com.e.cloud_login.Data.JSON.PhotoJson;
import com.e.cloud_login.Login_Funcation.AccountLogin;
import com.e.cloud_login.Main_Funcation.CanvasActivity;
import com.e.cloud_login.Main_Funcation.CloudActivity;
import com.e.cloud_login.Main_Funcation.HomeActivity;
import com.e.cloud_login.Main_Funcation.Pan_Funcation.PanRepositroy;
import com.e.cloud_login.Main_Funcation.SettingActivity;
import com.e.cloud_login.R;


import org.litepal.LitePal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;

import static android.app.Activity.RESULT_OK;
import static com.e.cloud_login.R.*;
import static com.e.cloud_login.R.id.default_activity_button;
import static com.e.cloud_login.R.id.fg_mine_head;
import static com.e.cloud_login.R.id.fg_mine_img_setting;
import static com.e.cloud_login.R.id.fg_mine_tv_setting;
import static com.e.cloud_login.R.id.start;

public class MineFragment extends BaseFragment {
    private AccountLogin accountService = new AccountLogin();
    private TextView tv_name,tv_sign,tv_cloud,tv_outlet,tv_service,tv_setting;
    private ImageView img_head,img_cloud,img_outlet,img_service,img_setting;
    private Button btn_file;
    private ResultListBeanAdapter adapter;
    private RecyclerView recyclerView ;
    private String LOGIN_STATE ="login_state";
    private File imageFile = null;//?????????????????????
    private Uri imageUri = null; //??????????????????URI
    private LinearLayout linearLayout;
    private final int REQUEST_CODE_GALLERY = 0x03;
    private final int CROP_PHOTO = 0x04;
    private FileListJson filelistBean;
    private PanRepositroy panRepositroy = new PanRepositroy();
    private SharedPreferences getuserinfo;
    private SharedPreferences.Editor writeuserinfo;
    private List<FileBean> beanList = new ArrayList<>();
    @Override
    public View initView() {
        View view = View.inflate(getActivity(), layout.fragment_mine,null);
        linearLayout = view.findViewById(id.fg_mine_top);
        tv_name = view.findViewById(R.id.fg_mine_name);
        tv_sign = view.findViewById(R.id.fg_mine_sign);
        tv_cloud =view.findViewById(R.id.fg_mine_tv_cloud);
        tv_outlet = view.findViewById(R.id.fg_mine_tv_outlet);
        tv_service = view.findViewById(R.id.fg_mine_tv_service);
        tv_setting = view.findViewById(R.id.fg_mine_tv_setting);
        img_head  = view.findViewById(R.id.fg_mine_head);
        img_cloud= view.findViewById(R.id.fg_mine_img_cloud);
        img_outlet = view.findViewById(R.id.fg_mine_img_outlet);
        img_service = view.findViewById(R.id.fg_mine_img_service);
        img_setting = view.findViewById(R.id.fg_mine_img_setting);
        btn_file = view.findViewById(R.id.fg_mine_btn_file);
        recyclerView = view.findViewById(id.fg_home_list);
        initData();
        setClickEvent();
        return view;
    }
    private void setClickEvent(){
        onClick onclick = new onClick();
        img_head.setOnClickListener(onclick);
        img_setting.setOnClickListener(onclick);
        tv_setting.setOnClickListener(onclick);
        img_cloud.setOnClickListener(onclick);
    }
    public class onClick implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            switch (v.getId()){
                case fg_mine_head:{
                    Log.d("head","?????????????????????");
                    gallery();
                    break;
                }
                case fg_mine_img_setting:{
                   intent.setClass(getContext(), SettingActivity.class);
                   startActivity(intent);
                   break;
                }
                case fg_mine_tv_setting:{
                    intent.setClass(getContext(), SettingActivity.class);
                    startActivity(intent);
                    break;
                }
                case id.fg_mine_img_cloud:{
                    startActivity(new Intent(getContext(), CloudActivity.class));
                }
            }
        }
    }
    @Override
    public void initData() {
        super.initData();
        LitePal.initialize(getContext());
        getuserinfo = getActivity().getSharedPreferences("userinfo",Context.MODE_PRIVATE);
        writeuserinfo = getActivity().getSharedPreferences("userinfo",Context.MODE_PRIVATE).edit();
        Animation animation = AnimationUtils.loadAnimation(getContext(), anim.file_fall_down);
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(getContext(),
                R.anim.layout_from_bottom);
        linearLayout.setAnimation(animation);
        //???????????????????????????listBean
        filelistBean = panRepositroy.getFileList(6,getuserinfo.getString("id",null),1,1);
        if(filelistBean!=null){
        beanList = filelistBean.fileList;
            adapter = new ResultListBeanAdapter(beanList);
            final Boolean[] longPress = {false};
            adapter.setOnItemClickListener(new ResultListBeanAdapter.MyItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    if (!longPress[0]) {//??????????????????
                        final String[] msg = new String[1];
                        FileBean fileBean = filelistBean.fileList.get(position);//??????????????????
                        String url = getContext().getExternalFilesDir(null).getPath();
                        File file = new File(url + "/DOWNLOAD_PDF/" + fileBean.fileName);
                        if (!file.exists()) {
                            AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                                    .setMessage("(??????"+fileBean.fileSizes+") ????????????")
                                    .setPositiveButton("???", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            msg[0] = panRepositroy.Download(url, fileBean);
                                            DownLoadBean downLoadBean = new DownLoadBean();
                                            downLoadBean.url = url + "/DOWNLOAD_PDF" + fileBean.fileName;
                                            downLoadBean.title = fileBean.fileTitle;
                                            downLoadBean.filePhoto = fileBean.filePhoto;
                                            downLoadBean.save();
                                            startActivity(new Intent(getContext(), CanvasActivity.class).putExtra("path", getContext().getExternalFilesDir(null).getPath()
                                                    + "/DOWNLOAD_PDF/" + fileBean.fileName));
                                            Toast.makeText(getContext(), msg[0], Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .setNegativeButton("???", null)
                                    .show();
                        } else {
                            startActivity(new Intent(getContext(), CanvasActivity.class)
                                    .putExtra("MineFragment",true)
                                    .putExtra("path", url + "/DOWNLOAD_PDF/" + fileBean.fileName));
                            Toast.makeText(getContext(), "success", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
            adapter.setMyItemLongClickListener(new ResultListBeanAdapter.MyItemLongClickListener() {
                @Override
                public void onItemLongClick(View view, int position) {
                    longPress[0] = true;
                    AlertDialog dialog_delete = new AlertDialog.Builder(getContext())
                            .setMessage("?????????????????????????????????")
                            .setPositiveButton("???", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String id = getuserinfo.getString("id", null);
                                    FileBean fileBean = beanList.get(position);
                                    String number = fileBean.number;
                                    String msg = panRepositroy.DeleteFile(id, number);
                                    if (msg == "success") {
                                        filelistBean = panRepositroy.getFileList(6,
                                                getuserinfo.getString("id", null), 1, 1);
                                        adapter.notifyDataSetChanged();
                                    }
                                    if (msg != null) {
                                        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getContext(), "????????????", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .setNegativeButton("???", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    longPress[0] = false;
                                }
                            })
                            .show();
                }

            });
            recyclerView.setAdapter(adapter);
        }
        else{
            Toast.makeText(getContext(),"???????????????",Toast.LENGTH_SHORT).show();
        }
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        recyclerView.addItemDecoration(new ResultListBeanAdapter.MyDecoration());
        recyclerView.setLayoutAnimation(controller);
        defaultLogin();
    }

    private void defaultLogin() {
        String photo = getuserinfo.getString("photo",null);
        Glide.with(getContext())
                .load(photo)
                .placeholder(drawable.cathead)
                .error(drawable.cathead)
                .centerCrop()
                .into(img_head);
        tv_name.setText(getuserinfo.getString("username","??????"));
        tv_sign.setText(getuserinfo.getString("introduction","????????????????????????????????????"));
    }

    /**
     * ???????????????????????????????????????
     */
    private void gallery(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,REQUEST_CODE_GALLERY);//?????????????????????url
    }

    /**
     * ????????????
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CODE_GALLERY:{
                   if(resultCode ==RESULT_OK){
                       Log.d("PORTRAIT_TEXT","??????????????????");
                       Uri uri = data.getData();//???????????????uri
                       Intent intent_gallery_crop = new Intent("com.android.camera.action.CROP");
                       intent_gallery_crop.setDataAndType(uri,"image/*");
                       //????????????
                       intent_gallery_crop.putExtra("crop","ture");
                       intent_gallery_crop.putExtra("scale",true);
                       //aspectX aspectY?????????????????????
                       intent_gallery_crop.putExtra("aspectX",1);
                       intent_gallery_crop.putExtra("aspectY",1);
                       //outputX outputY?????????????????????
                       intent_gallery_crop.putExtra("outputX",400);
                       intent_gallery_crop.putExtra("outputY",400);
                       intent_gallery_crop.putExtra("return-data",false);
                       //?????????????????????????????????
                       createImangeFile();
                       imageUri = Uri.fromFile(imageFile);
                       if(imageUri!=null){
                           intent_gallery_crop.putExtra(MediaStore.EXTRA_OUTPUT,
                                   imageUri);
                           intent_gallery_crop.putExtra("outputFormat",
                                   Bitmap.CompressFormat.JPEG.toString());
                       }
                       startActivityForResult(intent_gallery_crop,CROP_PHOTO);
                       break;
                   }
                }
            case CROP_PHOTO:{
                if(resultCode == RESULT_OK){
                    try {
                        if (imageUri!=null){
                            Log.d("TAG",imageUri.toString());
                            displayImage(imageUri);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
            default: break;
        }

        }

    /**
     * ????????????
      */
    private void displayImage(Uri imageUri){
        Log.d("PORTRAIT_TEXT","????????????");
        String id = getuserinfo.getString("id",null);
        PhotoJson result = accountService.accountPhoto(getContext(),imageUri,id);
        if(result.code == 200){
            Log.d("PORTRAIT_TEXT","??????????????????");
            Glide.with(getContext())
                    .load(imageUri)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(drawable.ic_launcher_background)
                    .error(drawable.cathead)
                    .centerCrop()
                    .into(img_head);
        }
        else {//???????????????????????????????????????????????????
            Log.d("PORTRAIT_TEXT","??????????????????");
            String PORTRAIT = "portrait.png";
            String filepath = "${accountService.PORTRAIT_PATH}/${AccountRepository.user?.username}${PORTRAIT}";
            File portraitImg = new File(filepath);
            if (portraitImg.exists()){
                Glide.with(getContext())
                        .load(imageUri)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .placeholder(drawable.ic_launcher_background)
                        .error(drawable.cathead)
                        .centerCrop()
                        .into(img_head);
            }
        }
        startActivity(new Intent(getContext(), HomeActivity.class));
    }

    /**
     * ??????File????????????,?????????????????????
     */
    private void createImangeFile(){
        try{
            if(imageFile !=null&&imageFile.exists()){
                imageFile.delete();
            }
            //????????????
            String PORTRAIT = "portrait.png";
           // Long time =System.currentTimeMillis();
            String phone = getuserinfo.getString("phone",null);
           // String filepath = "${accountService.PORTRAIT_PATH}/${phone}${PORTRAIT}";
            /**
             * ??????Context.getExternalFilesDir()????????????????????? SDCard/Android/data/?????????????????????/files/ ?????????
             * ???????????????????????????????????????
             * ??????Context.getExternalCacheDir()????????????????????? SDCard/Android/data/??????????????????/cache/?????????
             * ??????????????????????????????
             */
            imageFile = new File(getContext().getExternalFilesDir(null),
                    phone+PORTRAIT);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

