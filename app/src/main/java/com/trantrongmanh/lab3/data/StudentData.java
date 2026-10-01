package com.trantrongmanh.lab3.data;

import com.trantrongmanh.lab3.R;
import com.trantrongmanh.lab3.model.Student;

import java.util.ArrayList;
import java.util.List;

/**
 * Lớp cung cấp dữ liệu mẫu (dummy data) cho danh sách sinh viên.
 */
public class StudentData {

    /**
     * Trả về danh sách 10 sinh viên mẫu.
     */
    public static List<Student> getDummyStudents() {
        List<Student> list = new ArrayList<>();

        list.add(new Student(1, "Trần Trọng Mạnh",  "manh.tt@student.edu.vn",
                "15/03/2003", "Nam",  R.drawable.avatar_male_1));
        list.add(new Student(2, "Nguyễn Thị Lan",   "lan.nt@student.edu.vn",
                "22/07/2003", "Nữ",   R.drawable.avatar_female_1));
        list.add(new Student(3, "Lê Văn Hùng",      "hung.lv@student.edu.vn",
                "05/11/2002", "Nam",  R.drawable.avatar_male_2));
        list.add(new Student(4, "Phạm Thị Hoa",     "hoa.pt@student.edu.vn",
                "18/01/2003", "Nữ",   R.drawable.avatar_female_2));
        list.add(new Student(5, "Hoàng Minh Tuấn",  "tuan.hm@student.edu.vn",
                "30/06/2002", "Nam",  R.drawable.avatar_male_1));
        list.add(new Student(6, "Vũ Thị Thu",       "thu.vt@student.edu.vn",
                "14/09/2003", "Nữ",   R.drawable.avatar_female_1));
        list.add(new Student(7, "Đặng Quốc Bảo",   "bao.dq@student.edu.vn",
                "09/04/2002", "Nam",  R.drawable.avatar_male_2));
        list.add(new Student(8, "Bùi Thị Ngọc",    "ngoc.bt@student.edu.vn",
                "25/12/2003", "Nữ",   R.drawable.avatar_female_2));
        list.add(new Student(9, "Trịnh Văn Đức",   "duc.tv@student.edu.vn",
                "11/08/2002", "Nam",  R.drawable.avatar_male_1));
        list.add(new Student(10, "Lý Thị Mai",      "mai.lt@student.edu.vn",
                "03/02/2003", "Nữ",   R.drawable.avatar_female_1));

        return list;
    }

    /** Tạo id mới dựa trên id lớn nhất hiện có trong danh sách. */
    public static int generateNewId(List<Student> list) {
        int maxId = 0;
        for (Student s : list) {
            if (s.getId() > maxId) maxId = s.getId();
        }
        return maxId + 1;
    }
}
