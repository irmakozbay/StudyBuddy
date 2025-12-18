package msku.ceng.madproject.studybuddy;
import java.util.ArrayList;
import java.util.List;
public class GroupManager extends JoinGroupActivity {
    // Tüm uygulama boyunca tek bir liste tutar
    private static List<JoinGroupActivity.Group> groupList = new ArrayList<>();

    public static List<JoinGroupActivity.Group> getGroups() {
        if (groupList.isEmpty()) {
            // İlk açılışta varsayılan grupları ekleyelim
            groupList.add(new JoinGroupActivity.Group("Engineering Buddies", "Focused on calculus and physics.", R.drawable.profile));
            groupList.add(new JoinGroupActivity.Group("Study & Chill", "Collaborative study sessions.", R.drawable.profile));
            groupList.add(new JoinGroupActivity.Group("Exam Prep Squad","Focused on sharing notes, summaries, and tips before upcoming exams.",R.drawable.profile));
            groupList.add(new JoinGroupActivity.Group("Code & Coffee","For beginner programmers who love learning Java and Python with others.",R.drawable.profile));
            groupList.add(new JoinGroupActivity.Group("Math Wizards","Collaborate on solving calculus, algebra, and geometry problems.",R.drawable.profile));



        }
        return groupList;
    }

    public static void addGroup(JoinGroupActivity.Group group) {
        groupList.add(group);
    }
}