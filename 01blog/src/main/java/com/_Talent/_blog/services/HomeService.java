package com._Talent._blog.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import com._Talent._blog.model.*;

@Service
public class HomeService {

    // @Autowired
    private final Model mod;

    public HomeService(Model mod) {
        this.mod =mod;
    }

    
    //return just String data
    public  String Hometmplservice() {
        return mod.getname(1);
    }

    //return json data
    public Hometmp gethome(){
        Hometmp rtn = new Hometmp();
        rtn.setname("nmayyy");
        return rtn;
    }



    //return json array
    public List<Hometmp> gethomes(){
        ArrayList<Hometmp> all = new ArrayList<>();

        mod.getnames().forEach(nam -> all.add(new Hometmp(nam)));

        return all;

    }
}
