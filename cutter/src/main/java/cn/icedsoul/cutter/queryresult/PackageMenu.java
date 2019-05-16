package cn.icedsoul.cutter.queryresult;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class PackageMenu {
    Long id;
    String packageName;
    List<PackageMenu> children = new ArrayList<>();

    public PackageMenu(long id, String packageName){
        this.id = id;
        this.packageName = packageName;
    }

    public void addChild(PackageMenu pm){
        children.add(pm);
    }

}
