package com.zrd.rige.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zrd.rige.entity.AddressBook;
import com.zrd.rige.mapper.AddressBookMapper;
import com.zrd.rige.service.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
