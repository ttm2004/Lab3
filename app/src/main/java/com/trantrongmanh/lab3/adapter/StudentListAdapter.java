package com.trantrongmanh.lab3.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.trantrongmanh.lab3.R;
import com.trantrongmanh.lab3.model.Student;

import java.util.List;

/**
 * Custom BaseAdapter cho ListView sinh viên.
 * Áp dụng ViewHolder pattern để tối ưu hiệu năng.
 *
 * Sử dụng kiểu listener: Anonymous listener (onClickEdit / onClickDelete được set từ Fragment).
 */
public class StudentListAdapter extends BaseAdapter {

    // ── Interface callback cho các thao tác CRUD từ item ──────────────────────
    public interface OnItemActionListener {
        void onEdit(int position);
        void onDelete(int position);
        void onViewDetail(int position);
    }

    private final Context context;
    private List<Student> studentList;
    private OnItemActionListener actionListener;

    public StudentListAdapter(Context context, List<Student> studentList) {
        this.context = context;
        this.studentList = studentList;
    }

    public void setOnItemActionListener(OnItemActionListener listener) {
        this.actionListener = listener;
    }

    public void updateData(List<Student> newList) {
        this.studentList = newList;
        notifyDataSetChanged();
    }

    // ── BaseAdapter overrides ──────────────────────────────────────────────────

    @Override
    public int getCount() {
        return studentList == null ? 0 : studentList.size();
    }

    @Override
    public Student getItem(int position) {
        return studentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return studentList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_student_list, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Student student = studentList.get(position);
        bindData(holder, student, position);

        return convertView;
    }

    // ── Bind dữ liệu vào ViewHolder ───────────────────────────────────────────

    private void bindData(ViewHolder holder, Student student, int position) {
        holder.imgAvatar.setImageResource(student.getAvatarResId());
        holder.tvHoTen.setText(student.getHoTen());
        holder.tvEmail.setText(student.getEmail());
        holder.tvNgaySinh.setText(student.getNgaySinh());
        holder.tvGioiTinh.setText(student.getGioiTinh());

        // Đổi màu badge giới tính
        if ("Nam".equals(student.getGioiTinh())) {
            holder.tvGioiTinh.setTextColor(context.getColor(R.color.colorMale));
        } else {
            holder.tvGioiTinh.setTextColor(context.getColor(R.color.colorFemale));
        }

        // ── Anonymous listener: nút Sửa ───────────────────────────────────────
        holder.btnEdit.setOnClickListener(v -> {
            if (actionListener != null) actionListener.onEdit(position);
        });

        // ── Anonymous listener: nút Xóa ───────────────────────────────────────
        holder.btnDelete.setOnClickListener(v -> {
            if (actionListener != null) actionListener.onDelete(position);
        });

        // Click item → xem chi tiết
        holder.itemView.setOnClickListener(v -> {
            if (actionListener != null) actionListener.onViewDetail(position);
        });
    }

    // ── ViewHolder pattern ─────────────────────────────────────────────────────

    static class ViewHolder {
        final View itemView;
        final ImageView imgAvatar;
        final TextView tvHoTen;
        final TextView tvEmail;
        final TextView tvNgaySinh;
        final TextView tvGioiTinh;
        final ImageButton btnEdit;
        final ImageButton btnDelete;

        ViewHolder(View view) {
            itemView = view;
            imgAvatar  = view.findViewById(R.id.imgAvatar);
            tvHoTen    = view.findViewById(R.id.tvHoTen);
            tvEmail    = view.findViewById(R.id.tvEmail);
            tvNgaySinh = view.findViewById(R.id.tvNgaySinh);
            tvGioiTinh = view.findViewById(R.id.tvGioiTinh);
            btnEdit    = view.findViewById(R.id.btnEdit);
            btnDelete  = view.findViewById(R.id.btnDelete);
        }
    }
}
