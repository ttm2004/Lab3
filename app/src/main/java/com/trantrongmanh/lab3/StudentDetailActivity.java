package com.trantrongmanh.lab3;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.trantrongmanh.lab3.model.Student;

/**
 * Activity hiển thị chi tiết thông tin một sinh viên.
 * Nhận đối tượng Student qua Intent (Serializable).
 */
public class StudentDetailActivity extends AppCompatActivity {

    public static final String EXTRA_STUDENT = "extra_student";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_detail);

        // Thiết lập Toolbar với nút back
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // Lấy dữ liệu từ Intent
        Student student = (Student) getIntent().getSerializableExtra(EXTRA_STUDENT);
        if (student == null) {
            finish();
            return;
        }

        // Bind dữ liệu lên View
        ImageView imgAvatar    = findViewById(R.id.imgAvatar);
        TextView  tvHoTen      = findViewById(R.id.tvHoTen);
        TextView  tvEmail      = findViewById(R.id.tvEmail);
        TextView  tvNgaySinh   = findViewById(R.id.tvNgaySinh);
        TextView  tvGioiTinh   = findViewById(R.id.tvGioiTinh);
        TextView  tvId         = findViewById(R.id.tvId);

        imgAvatar.setImageResource(student.getAvatarResId());
        tvHoTen.setText(student.getHoTen());
        tvEmail.setText(student.getEmail());
        tvNgaySinh.setText(student.getNgaySinh());
        tvGioiTinh.setText(student.getGioiTinh());
        tvId.setText(String.valueOf(student.getId()));

        if ("Nam".equals(student.getGioiTinh())) {
            tvGioiTinh.setTextColor(getColor(R.color.colorMale));
        } else {
            tvGioiTinh.setTextColor(getColor(R.color.colorFemale));
        }
    }
}
