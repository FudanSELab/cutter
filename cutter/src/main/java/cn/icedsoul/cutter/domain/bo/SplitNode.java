package cn.icedsoul.cutter.domain.bo;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public class SplitNode {

    private Long id;
    private String name;
    private int level;//1:package 2:class 3:method 4:sql
    private List<SplitNode> children = new ArrayList<>();

    public SplitNode(long id, String name, int level){
        this.id = id;
        this.name = name;
        this.level = level;
    }

    public void addNode(SplitNode sn){
        children.add(sn);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public List<SplitNode> getChildren() {
        return children;
    }

    public void setChildren(List<SplitNode> children) {
        this.children = children;
    }


    @Override
    public boolean equals(Object o){
        if(o instanceof SplitNode){
            if(((SplitNode)o).getLevel() == this.level && ((SplitNode)o).getId() == this.id
                    && ((SplitNode)o).getName() == this.name) return true;
        }
        return false;
    }

    @Override
    public String toString(){
        return  "{id:" + this.id + " name:" + this.name+"}";
    }


}
