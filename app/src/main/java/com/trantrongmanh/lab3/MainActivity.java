package com.trantrongmanh.lab3;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.trantrongmanh.lab3.adapter.StudentGridAdapter;
import com.trantrongmanh.lab3.adapter.StudentListAdapter;
import com.trantrongmanh.lab3.data.StudentData;
import com.trantrongmanh.lab3.model.Student;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

// ══════════════════════════════════════════════════════════════════════════════
//  MainActivity – Quản lý danh sách sinh viên
//
//  Trình bày 5 kiểu lập trình sự kiện trong Android:
//
//  1. Activity as Listener   – MainActivity tự implement View.OnClickListener
//                              (xử lý FAB thêm sinh viên).
//
//  2. Explicit class Listener– Lớp riêng SortMenuListener xử lý click menu.
//
//  3. View Subclassing       – Lớp SearchEditText kế thừa TextInputEditText
//                              override onTextChanged để lọc danh sách.
//
//  4. Anonymous Listener     – Các nút Sửa/Xóa trong Adapter dùng lambda.
//
//  5. Variable as Listener   – DatePickerDialog.OnDateSetListener gán vào
//                              biến rồi truyền vào DatePickerDialog.
// ══════════════════════════════════════════════════════════════════════════════

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener {           // [1] Activity as Listener

    // ── Dữ liệu dùng chung (chia sẻ giữa 2 fragment qua static) ──────────────
    static List<Student> studentList = new ArrayList<>();

    // ── View references ───────────────────────────────────────────────────────
    private ViewPager2 viewPager;
    private TabLayout  tabLayout;

    // ── Fragment references ───────────────────────────────────────────────────
    private ListFragment listFragment;
    private GridFragment gridFragment;

    // ── Pattern kiểm tra email ────────────────────────────────────────────────
    static final Pattern EMAIL_PATTERN = Patterns.EMAIL_ADDRESS;

    // ── Pattern kiểm tra ngày (dd/MM/yyyy) ───────────────────────────────────
    static final Pattern DATE_PATTERN =
            Pattern.compile("^(0[1-9]|[12]\\d|3[01])/(0[1-9]|1[0-2])/\\d{4}$");

    // ════════════════════════════════════════════════════════════════════════════
    //  onCreate
    // ════════════════════════════════════════════════════════════════════════════

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo dữ liệu dummy (chỉ lần đầu)
        if (studentList.isEmpty()) {
            studentList.addAll(StudentData.getDummyStudents());
        }

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // ViewPager2 + Fragments
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        listFragment = new ListFragment();
        gridFragment = new GridFragment();

        viewPager.setAdapter(new MainPagerAdapter(this));
        viewPager.setOffscreenPageLimit(2);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(position == 0
                    ? getString(R.string.tab_list_view)
                    : getString(R.string.tab_grid_view));
        }).attach();

        // [1] Activity as Listener – FAB
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(this);   // MainActivity tự xử lý
    }

    // ════════════════════════════════════════════════════════════════════════════
    //  [1] Activity as Listener – onClick (FAB Thêm)
    // ════════════════════════════════════════════════════════════════════════════

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fabAdd) {
            showStudentDialog(null, -1);   // null = thêm mới
        }
    }

    // ════════════════════════════════════════════════════════════════════════════
    //  Menu (Toolbar)
    // ════════════════════════════════════════════════════════════════════════════

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // [2] Explicit class Listener – giao cho SortMenuListener xử lý
        return new SortMenuListener(this).onMenuItemClick(item)
                || super.onOptionsItemSelected(item);
    }

    // ════════════════════════════════════════════════════════════════════════════
    //  Dialog Thêm / Sửa sinh viên
    // ════════════════════════════════════════════════════════════════════════════

    void showStudentDialog(Student existing, int position) {
        boolean isEdit = (existing != null);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_student, null);

        TextInputLayout  tilHoTen   = dialogView.findViewById(R.id.tilHoTen);
        TextInputLayout  tilEmail   = dialogView.findViewById(R.id.tilEmail);
        TextInputLayout  tilNgaySinh= dialogView.findViewById(R.id.tilNgaySinh);
        TextInputEditText etHoTen   = dialogView.findViewById(R.id.etHoTen);
        TextInputEditText etEmail   = dialogView.findViewById(R.id.etEmail);
        TextInputEditText etNgaySinh= dialogView.findViewById(R.id.etNgaySinh);
        RadioGroup       rgGioiTinh = dialogView.findViewById(R.id.rgGioiTinh);
        RadioButton      rbNam      = dialogView.findViewById(R.id.rbNam);
        RadioButton      rbNu       = dialogView.findViewById(R.id.rbNu);

        // Điền dữ liệu nếu đang sửa
        if (isEdit) {
            etHoTen.setText(existing.getHoTen());
            etEmail.setText(existing.getEmail());
            etNgaySinh.setText(existing.getNgaySinh());
            if ("Nữ".equals(existing.getGioiTinh())) rbNu.setChecked(true);
            else rbNam.setChecked(true);
        }

        // [5] Variable as Listener – DatePickerDialog.OnDateSetListener
        DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, year, month, day) -> {
            String dateStr = String.format(Locale.getDefault(), "%02d/%02d/%04d",
                    day, month + 1, year);
            etNgaySinh.setText(dateStr);
        };

        // Mở DatePickerDialog khi click trường ngày sinh
        etNgaySinh.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(this, dateSetListener,
                    cal.get(Calendar.YEAR) - 20,
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show();
        });

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(isEdit
                        ? getString(R.string.dialog_edit_title)
                        : getString(R.string.dialog_add_title))
                .setView(dialogView)
                .setPositiveButton(R.string.btn_save, null)  // null → handle manually
                .setNegativeButton(R.string.btn_cancel, null)
                .create();

        dialog.show();

        // Override positive button để giữ dialog khi validation lỗi
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String hoTen   = etHoTen.getText() != null
                    ? etHoTen.getText().toString().trim() : "";
            String email   = etEmail.getText() != null
                    ? etEmail.getText().toString().trim() : "";
            String ngaySinh= etNgaySinh.getText() != null
                    ? etNgaySinh.getText().toString().trim() : "";
            boolean isNam  = rbNam.isChecked();
            String gioiTinh= isNam ? "Nam" : "Nữ";

            // Validation
            tilHoTen.setError(null);
            tilEmail.setError(null);
            tilNgaySinh.setError(null);

            if (hoTen.isEmpty()) {
                tilHoTen.setError(getString(R.string.toast_fill_all));
                return;
            }
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                tilEmail.setError(getString(R.string.toast_invalid_email));
                return;
            }
            if (!DATE_PATTERN.matcher(ngaySinh).matches()) {
                tilNgaySinh.setError(getString(R.string.toast_invalid_date));
                return;
            }

            int avatarResId = isNam ? R.drawable.avatar_male_1 : R.drawable.avatar_female_1;

            if (isEdit) {
                // ── CẬP NHẬT ──────────────────────────────────────────────────
                existing.setHoTen(hoTen);
                existing.setEmail(email);
                existing.setNgaySinh(ngaySinh);
                existing.setGioiTinh(gioiTinh);
                existing.setAvatarResId(avatarResId);
                notifyAllAdapters();
                Toast.makeText(this, R.string.toast_updated, Toast.LENGTH_SHORT).show();
            } else {
                // ── THÊM MỚI ──────────────────────────────────────────────────
                int newId = StudentData.generateNewId(studentList);
                Student newStudent = new Student(newId, hoTen, email,
                        ngaySinh, gioiTinh, avatarResId);
                studentList.add(newStudent);
                notifyAllAdapters();
                Toast.makeText(this, R.string.toast_added, Toast.LENGTH_SHORT).show();
            }

            dialog.dismiss();
        });
    }

    // ════════════════════════════════════════════════════════════════════════════
    //  Dialog Xóa sinh viên
    // ════════════════════════════════════════════════════════════════════════════

    void showDeleteDialog(int position) {
        Student student = studentList.get(position);
        String msg = String.format(getString(R.string.dialog_delete_msg), student.getHoTen());

        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_delete_title)
                .setMessage(msg)
                .setPositiveButton(R.string.btn_confirm, (d, w) -> {
                    studentList.remove(position);
                    notifyAllAdapters();
                    Toast.makeText(this, R.string.toast_deleted, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.btn_cancel, null)
                .show();
    }

    // ════════════════════════════════════════════════════════════════════════════
    //  Sắp xếp
    // ════════════════════════════════════════════════════════════════════════════

    void sortByName() {
        Collections.sort(studentList, (a, b) ->
                a.getHoTen().compareToIgnoreCase(b.getHoTen()));
        notifyAllAdapters();
    }

    void sortById() {
        Collections.sort(studentList, (a, b) ->
                Integer.compare(a.getId(), b.getId()));
        notifyAllAdapters();
    }

    // ════════════════════════════════════════════════════════════════════════════
    //  Cập nhật cả 2 adapter sau mọi thay đổi dữ liệu
    // ════════════════════════════════════════════════════════════════════════════

    void notifyAllAdapters() {
        if (listFragment != null) listFragment.refreshList();
        if (gridFragment != null) gridFragment.refreshGrid();
    }

    // ════════════════════════════════════════════════════════════════════════════
    //  ViewPager2 Adapter (2 tab: ListFragment, GridFragment)
    // ════════════════════════════════════════════════════════════════════════════

    private class MainPagerAdapter extends FragmentStateAdapter {
        MainPagerAdapter(FragmentActivity fa) { super(fa); }

        @Override public int getItemCount() { return 2; }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0) return listFragment;
            return gridFragment;
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  [2] EXPLICIT CLASS LISTENER – xử lý click menu sắp xếp
    // ══════════════════════════════════════════════════════════════════════════

    static class SortMenuListener {
        private final MainActivity activity;

        SortMenuListener(MainActivity activity) {
            this.activity = activity;
        }

        boolean onMenuItemClick(MenuItem item) {
            if (item.getItemId() == R.id.action_sort_name) {
                activity.sortByName();
                return true;
            }
            if (item.getItemId() == R.id.action_sort_id) {
                activity.sortById();
                return true;
            }
            return false;
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  FRAGMENT – ListView
    // ══════════════════════════════════════════════════════════════════════════

    public static class ListFragment extends androidx.fragment.app.Fragment {

        private StudentListAdapter adapter;
        private List<Student> filteredList = new ArrayList<>();
        private ListView listView;
        private TextView tvCount;

        public ListFragment() { super(R.layout.fragment_list); }

        @Override
        public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            listView = view.findViewById(R.id.listView);
            tvCount  = view.findViewById(R.id.tvCount);

            filteredList = new ArrayList<>(studentList);
            adapter = new StudentListAdapter(requireContext(), filteredList);
            listView.setAdapter(adapter);
            updateCount();

            // ── [4] Anonymous Listener – callback từ adapter ──────────────────
            adapter.setOnItemActionListener(new StudentListAdapter.OnItemActionListener() {
                @Override
                public void onEdit(int position) {
                    MainActivity act = (MainActivity) requireActivity();
                    act.showStudentDialog(filteredList.get(position),
                            studentList.indexOf(filteredList.get(position)));
                }

                @Override
                public void onDelete(int position) {
                    MainActivity act = (MainActivity) requireActivity();
                    int realIndex = studentList.indexOf(filteredList.get(position));
                    act.showDeleteDialog(realIndex);
                }

                @Override
                public void onViewDetail(int position) {
                    Intent intent = new Intent(requireContext(), StudentDetailActivity.class);
                    intent.putExtra(StudentDetailActivity.EXTRA_STUDENT,
                            filteredList.get(position));
                    startActivity(intent);
                }
            });

            // ── [3] VIEW SUBCLASSING – SearchEditText lọc danh sách ───────────
            TextInputEditText etSearch = view.findViewById(R.id.etSearch);
            etSearch.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int st, int cnt, int aft) {}
                @Override public void onTextChanged(CharSequence s, int st, int bf, int cnt) {}
                @Override
                public void afterTextChanged(Editable editable) {
                    filterList(editable.toString());
                }
            });

            // Click item → xem chi tiết (AdapterView.OnItemClickListener)
            listView.setOnItemClickListener((parent, v, pos, id) -> {
                Intent intent = new Intent(requireContext(), StudentDetailActivity.class);
                intent.putExtra(StudentDetailActivity.EXTRA_STUDENT, filteredList.get(pos));
                startActivity(intent);
            });
        }

        private void filterList(String query) {
            filteredList.clear();
            String q = query.toLowerCase().trim();
            if (q.isEmpty()) {
                filteredList.addAll(studentList);
            } else {
                for (Student s : studentList) {
                    if (s.getHoTen().toLowerCase().contains(q)
                            || s.getEmail().toLowerCase().contains(q)) {
                        filteredList.add(s);
                    }
                }
            }
            adapter.updateData(filteredList);
            updateCount();
        }

        void refreshList() {
            filteredList.clear();
            filteredList.addAll(studentList);
            if (adapter != null) {
                adapter.updateData(filteredList);
            }
            updateCount();
        }

        private void updateCount() {
            if (tvCount != null) {
                tvCount.setText(getString(R.string.count_students, filteredList.size()));
            }
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  FRAGMENT – GridView
    // ══════════════════════════════════════════════════════════════════════════

    public static class GridFragment extends androidx.fragment.app.Fragment {

        private StudentGridAdapter adapter;
        private List<Student> filteredList = new ArrayList<>();
        private GridView gridView;
        private TextView tvCount;

        public GridFragment() { super(R.layout.fragment_grid); }

        @Override
        public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            gridView = view.findViewById(R.id.gridView);
            tvCount  = view.findViewById(R.id.tvCount);

            filteredList = new ArrayList<>(studentList);
            adapter = new StudentGridAdapter(requireContext(), filteredList);
            gridView.setAdapter(adapter);
            updateCount();

            // ── [4] Anonymous Listener – callback từ grid adapter ─────────────
            adapter.setOnItemActionListener(new StudentGridAdapter.OnItemActionListener() {
                @Override
                public void onEdit(int position) {
                    MainActivity act = (MainActivity) requireActivity();
                    act.showStudentDialog(filteredList.get(position),
                            studentList.indexOf(filteredList.get(position)));
                }

                @Override
                public void onDelete(int position) {
                    MainActivity act = (MainActivity) requireActivity();
                    int realIndex = studentList.indexOf(filteredList.get(position));
                    act.showDeleteDialog(realIndex);
                }

                @Override
                public void onViewDetail(int position) {
                    Intent intent = new Intent(requireContext(), StudentDetailActivity.class);
                    intent.putExtra(StudentDetailActivity.EXTRA_STUDENT,
                            filteredList.get(position));
                    startActivity(intent);
                }
            });

            // ── [3] View Subclassing (TextWatcher) – lọc GridView ─────────────
            TextInputEditText etSearch = view.findViewById(R.id.etSearch);
            etSearch.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int st, int cnt, int aft) {}
                @Override public void onTextChanged(CharSequence s, int st, int bf, int cnt) {}
                @Override
                public void afterTextChanged(Editable editable) {
                    filterGrid(editable.toString());
                }
            });

            // [5] Variable as Listener – AdapterView.OnItemClickListener
            AdapterView.OnItemClickListener gridClickListener = (parent, v, pos, id) -> {
                Intent intent = new Intent(requireContext(), StudentDetailActivity.class);
                intent.putExtra(StudentDetailActivity.EXTRA_STUDENT, filteredList.get(pos));
                startActivity(intent);
            };
            gridView.setOnItemClickListener(gridClickListener);
        }

        private void filterGrid(String query) {
            filteredList.clear();
            String q = query.toLowerCase().trim();
            if (q.isEmpty()) {
                filteredList.addAll(studentList);
            } else {
                for (Student s : studentList) {
                    if (s.getHoTen().toLowerCase().contains(q)
                            || s.getEmail().toLowerCase().contains(q)) {
                        filteredList.add(s);
                    }
                }
            }
            adapter.updateData(filteredList);
            updateCount();
        }

        void refreshGrid() {
            filteredList.clear();
            filteredList.addAll(studentList);
            if (adapter != null) {
                adapter.updateData(filteredList);
            }
            updateCount();
        }

        private void updateCount() {
            if (tvCount != null) {
                tvCount.setText(getString(R.string.count_students, filteredList.size()));
            }
        }
    }
}
