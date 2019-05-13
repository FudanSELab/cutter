package cn.icedsoul.cutter;

import cn.icedsoul.cutter.domain.Package;
import cn.icedsoul.cutter.queryresult.PackageMenu;
import cn.icedsoul.cutter.repository.PackageContainRepository;
import cn.icedsoul.cutter.repository.PackageRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GetPackageStructureTest {
    @Autowired
    PackageRepository packageRepository;

    @Test
    public void testMethod() {
        Package root = packageRepository.findByFullPackageName("Root");
        Queue< PackageMenu> queue = new LinkedList<>();
        PackageMenu rootMenu = new PackageMenu();
        if(root != null) {
            rootMenu  = new PackageMenu(root.getId(),root.getPackageName());
            queue.offer(rootMenu);
        }
        while(!queue.isEmpty()){
            PackageMenu parent = queue.poll();
            List<Package> children = packageRepository.findChildrenByPackageId(parent.getId());
            if(children != null && children.size() > 0){
                for(Package child: children){
                    PackageMenu pm = new PackageMenu(child.getId(), child.getPackageName());
                    parent.addChild(pm);
                    queue.offer(pm);
                }
            }
        }
        printPackageMenu(rootMenu,0);
    }

    public void printPackageMenu(PackageMenu root, int level){
        for(int i = 0; i < level; i++){
            System.out.print("\t");
        }
        System.out.print("|-");
        System.out.println("(" + root.getId() + ")" + root.getPackageName() );
        for(PackageMenu pm: root.getChildren()){
            printPackageMenu(pm, level +1);
        }
    }

}
