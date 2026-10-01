package com.trantrongmanh.lab3.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.trantrongmanh.lab3.R;
import com.trantrongmanh.lab3.model.Student;

import java.util.List;

/**
 * Custom BaseAdapter cho GridView sinh viên (2 cột).
 * Dùng chung interface OnItemActionListener với StudentListAdapter.
 */
public class StudentGridAdapter extends BaseAdapter {

    public interface OnItemActionListener {
        void onEdit(int position);
        void onDelete(int position);
        void onViewDetail(int position);
    }

    private final Context context;
    private List<Student> studentList;
    private OnItemActionListener actionListener;

    public StudentGridAdapter(Context context, List<Student> studentList) {
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

    @Override public int getCount()               { return studentList == null ? 0 : studentList.size(); }
    @Override public Student getItem(int pos)     { return studentList.get(pos); }
    @Override public long getItemId(int pos)      { return studentList.get(pos).getId(); }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_student_grid, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Student student = studentList.get(position);

        holder.imgAvatar.setImageResource(student.getAvatarResId());
        holder.tvHoTen.setText(student.getHoTen());
        holder.tvEmail.setText(student.getEmail());
        holder.tvNgaySinh.setText(student.getNgaySinh());
        holder.tvGioiTinh.setText(student.getGioiTinh());

        if ("Nam".equals(student.getGioiTinh())) {
            holder.tvGioiTinh.setTextColor(context.getColor(R.color.colorMale));
        } else {
            holder.tvGioiTinh.setTextColor(context.getColor(R.color.colorFemale));
        }

        // ── Anonymous listener ─────────────────────────────────────────────────
        holder.btnEdit.setOnClickListener(v -> {
            if (actionListener != null) actionListener.onEdit(position);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (actionListener != null) actionListener.onDelete(position);
        });

        convertView.setOnClickListener(v -> {
            if (actionListener != null) actionListener.onViewDetail(position);
        });

        return convertView;
    }

    static class ViewHolder {
        final ImageView imgAvatar;
        final TextView tvHoTen;
        final TextView tvEmail;
        final TextView tvNgaySinh;
        final TextView tvGioiTinh;
        final Button btnEdit;
        final Button btnDelete;

        ViewHolder(View view) {
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
