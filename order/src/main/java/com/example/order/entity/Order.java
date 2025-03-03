package com.example.order.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "orders")
@AttributeOverride(name = "createdDate", column = @Column(name = "order_time", updatable = false))
@AttributeOverride(name = "deletedDate", column = @Column(name = "cancel_time"))
@AttributeOverride(name = "updatedDate", column = @Column(name = "update_time"))
@Where(clause = "cancel_time is null")
public class Order extends AbstractDate implements Serializable {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

    @Column(name = "destination_address")
    private String destinationAddress;

    @Column(name = "user_id")
    private Long userId;

    //@NotNull(message = "This field cannot be null!")
    private boolean completed = false;

    @JsonIgnore
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<OrderDetail> orderDetails;
    public boolean getCompleted(){
        return this.completed;
    }

}
