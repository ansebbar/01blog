package com._Talent._blog.model;

import java.util.ArrayList;
import java.util.List;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class Model {
    List<String> name = new ArrayList<>();

    Model() {
        name.add("anas");
        name.add("khadija");
    }

    public String getname(int index) {
        // stream search for it 
        return name.get(index);
        // return this.name.get(index);
    }
    public List<String> getnames() {
        return name;
    }
}
