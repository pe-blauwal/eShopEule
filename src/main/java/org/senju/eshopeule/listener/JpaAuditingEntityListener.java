package org.senju.eshopeule.listener;

import jakarta.persistence.PrePersist;
import org.senju.eshopeule.model.AbstractAuditEntity;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Configurable
public class JpaAuditingEntityListener extends AuditingEntityListener {

    public JpaAuditingEntityListener(@Qualifier("jpaAuditingHandler") ObjectFactory<AuditingHandler> handler) {
        super.setAuditingHandler(handler);
    }

    @Override
    @PrePersist
    public void touchForCreate(Object target) {
        AbstractAuditEntity entity = (AbstractAuditEntity) target;
        if (entity.getCreatedBy() == null) {
            super.touchForCreate(target);
        } else {
            if (entity.getLastModifiedBy() == null) {
                entity.setLastModifiedBy(entity.getCreatedBy());
            }
        }
    }

    @Override
    public void touchForUpdate(Object target) {
        AbstractAuditEntity entity = (AbstractAuditEntity) target;
        if (entity.getLastModifiedBy() == null) {
            super.touchForUpdate(target);
        }
    }
}
