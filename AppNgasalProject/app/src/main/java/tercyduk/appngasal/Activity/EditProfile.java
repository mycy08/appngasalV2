package tercyduk.appngasal.Activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tercyduk.appngasal.Main2Activity;
import tercyduk.appngasal.R;
import tercyduk.appngasal.apihelper.APIClient;
import tercyduk.appngasal.apihelper.LoginService;
import tercyduk.appngasal.apihelper.UserClient;
import tercyduk.appngasal.coresmodel.LapanganFutsal;
import tercyduk.appngasal.coresmodel.User;
import tercyduk.appngasal.modules.auth.user.Login;
import tercyduk.appngasal.modules.auth.user.Register;

public class EditProfile extends AppCompatActivity {
    EditText etName,    etAlamat, etemail, etNohp;
    String id,name,alamat,no_hp;
    ImageView imgEdp;
    TextView txtLogin;
    Button btnUpdate;
    Context mContext;
    AlertDialog alertDialog;
    AlertDialog.Builder alertDialogBuilder;
    private User user;
    public static final int PICK_IMAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        initComponents();
        setTitle("EditProfile");


    }

    /**
     * Dispatch incoming result to the correct fragment.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final Intent intentImg = getIntent();
        final String tokenImg = intentImg.getStringExtra("token");
        final String emailImg = intentImg.getStringExtra("email");
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {

            android.net.Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            android.database.Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            if (cursor == null)
                return;

            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String filePath = cursor.getString(columnIndex);
            cursor.close();

            File file = new File(filePath);
            UserClient userClient= APIClient.getClient().create(UserClient.class);

            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("upload", file.getName(), reqFile);
            RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "upload_test");

//            Log.d("THIS", data.getData().getPath());

            retrofit2.Call<okhttp3.ResponseBody> req = userClient.postImage(body, name);
            req.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }

    private void initComponents() {
        final Intent inten = getIntent();
        final String token = inten.getStringExtra("token");
        final String email = inten.getStringExtra("email");
        //Toast.makeText(getApplicationContext(),token.toString(), Toast.LENGTH_SHORT).show();

        UserClient userClient= APIClient.getClient().create(UserClient.class);
        Call<User> call = userClient.find("Bearer "+token, email);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, retrofit2.Response<User> response) {
                if(response.body() != null){
                    User users= response.body();
                    id = users.getId();
                    if(users.getPhoto() != null)
                    {

                        imgEdp = (CircularImageView) findViewById(R.id.edp_photo_profil);
                        Picasso.with(getApplicationContext()).load(users.getPhoto()).into(imgEdp);
                    }
                    else
                    {
                        imgEdp = (CircularImageView)findViewById(R.id.edp_photo_profil);
                        imgEdp.setImageResource(R.mipmap.profile);
                    }

                    etNohp = (EditText) findViewById(R.id.edp_txt_hp);
                    etNohp.setText(users.getPhone_number());
                    etAlamat = (EditText) findViewById(R.id.edp_txt_alamat);
                    etAlamat.setText(users.getAddress());
                    etName = (EditText) findViewById(R.id.edp_txt_name);
                    etName.setText(users.getName());

                }

            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                alertDialogBuilder.setMessage("Jaringan Sedang Bermasalah").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        ImageView btn = (CircularImageView) findViewById(R.id.edp_upload_image_profil);
        if (btn != null) {
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setType("image/user/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE);
                }
            });
        }



       btnUpdate = (Button) findViewById(R.id.btnCreateAccount);
       btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean _isvalid = true;
                if(TextUtils.isEmpty(etName.getText()))
                {
                    _isvalid = false;
                    etName.setError("Name is required");
                }
                else if (etName.getText().length() < 7) {
                    _isvalid = false;
                    etName.setError("Name minimal 7");
                }
                else if(TextUtils.isEmpty(etAlamat.getText()))
                {
                    _isvalid = false;
                    etAlamat.setError("Address is required");
                }
                else if(TextUtils.isEmpty(etNohp.getText()))
                {
                    _isvalid = false;
                    etNohp.setError("Handphone is required");
                }
                if(_isvalid)
                {
                    try {

                         name = etName.getText().toString();
                         alamat= etAlamat.getText().toString();
                         no_hp = etNohp.getText().toString();
                        UserClient userClient= APIClient.getClient().create(UserClient.class);
                        Call call = userClient.update("Bearer "+token,id,name,no_hp,alamat);
                        call.enqueue(new Callback() {
                            @Override
                            public void onResponse(Call call, Response response) {

                                if (response.isSuccessful()) {
                                    Intent intentToProfil = new Intent(EditProfile.this, Profil.class);
                                    intentToProfil.putExtra("token", token);
                                    intentToProfil.putExtra("email", email);
                                    startActivity(intentToProfil);
//
                                }else {
                                    Toast.makeText(getApplicationContext(), "Ga tau", Toast.LENGTH_SHORT).show();
                                    alertDialogBuilder.setMessage("Jaringan Sedang Bermasalah").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });
                                    alertDialog = alertDialogBuilder.create();
                                    alertDialog.show();
                                }
                            }

                            @Override
                            public void onFailure(Call call, Throwable t) {
                                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                                alertDialogBuilder.setMessage("Jaringan Sedang Bermasalah").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                                alertDialog = alertDialogBuilder.create();
                                alertDialog.show();
                            }

                        });
//

                    }
                    catch (Exception e) {

                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }

            }

        });

    }

    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
    public static void doChangeActivity (Context context, Class destination) {
        Intent _intent = new Intent(context, destination);
        _intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(_intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menus, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.homes:
                final Intent inten = getIntent();
                final String token = inten.getStringExtra("token");
                final String email = inten.getStringExtra("email");
                Intent intentToMainmenu = new Intent(EditProfile.this, Main2Activity.class);
                intentToMainmenu.putExtra("token", token);
                intentToMainmenu.putExtra("email", email);
                startActivity(intentToMainmenu);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

}