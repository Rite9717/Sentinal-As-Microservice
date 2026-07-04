package com.sentinal.instance.repository;

import com.sentinal.instance.model.InstanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface InstanceRepository extends JpaRepository<InstanceEntity, Long>
{
    boolean existsByInstanceIdAndOwnerUserId(String instanceId, Long ownerUserId);
    List<InstanceEntity> findByOwnerUserIdOrderByCreatedAtDesc(Long ownerUserId);
    Optional<InstanceEntity> findByIdAndOwnerUserId(Long instanceId, Long ownerId);

}
