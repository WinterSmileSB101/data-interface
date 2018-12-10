package cn.shaines.datainterface.service;

import cn.shaines.datainterface.entity.VisitLog;
import cn.shaines.datainterface.repository.VisitLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @program: data-interface
 * @description:
 * @author: houyu
 * @create: 2018-12-06 20:57
 */
@Service
public class VisitLogServerImpl implements VisitLogServer {

    @Autowired
    private VisitLogRepository visitLogRepository;

    @Override
    public VisitLog save(VisitLog visitLog) {
        return visitLogRepository.save(visitLog);
    }

    @Override
    public VisitLog updata(VisitLog visitLog) {
        return visitLogRepository.save(visitLog);
    }

    @Override
    public Page<VisitLog> findAll(Pageable pageable) {
//        Sort sort = new Sort(Sort.Direction.DESC,"readSize","commentSize","voteSize","createTime");
//        if (pageable.getSort() == null) {
//            pageable = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), sort);
//        }
        return visitLogRepository.findAll(pageable);
    }

    @Override
    public List<VisitLog> findAll() {
        return visitLogRepository.findAll();
    }

    @Override
    public VisitLog findById(String id) {
        return visitLogRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteAll() {
        visitLogRepository.deleteAll();
    }

    @Override
    public void deleteById(String id) {
        visitLogRepository.deleteById(id);
    }

}
