package com.example.pos_backend.controller;

import com.example.pos_backend.entity.Store;
import com.example.pos_backend.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stores")
public class StoreController {

    @Autowired
    private StoreRepository storeRepository;

    // 获取所有门店
    @GetMapping
    public List<Store> getAll() {
        return storeRepository.findAll();
    }

    // 新增门店
    @PostMapping
    public Store create(@RequestBody Store store) {
        return storeRepository.save(store);
    }

    // 根据 ID 获取门店
    @GetMapping("/{id}")
    public Store getById(@PathVariable Long id) {
        return storeRepository.findById(id).orElseThrow();
    }

    // 更新门店
    @PutMapping("/{id}")
    public Store update(@PathVariable Long id, @RequestBody Store updated) {
        Store store = storeRepository.findById(id).orElseThrow();
        store.setName(updated.getName());
        store.setAddress(updated.getAddress());
        store.setPhone(updated.getPhone());
        store.setStatus(updated.getStatus());
        return storeRepository.save(store);
    }

    // 删除门店（软删除）
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        Store store = storeRepository.findById(id).orElseThrow();
        store.setIsDeleted(true);
        storeRepository.save(store);
    }
}
