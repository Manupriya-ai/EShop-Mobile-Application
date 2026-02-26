package lk.tnm.eshop.fragment;

import static android.telecom.PhoneAccount.builder;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import lk.tnm.eshop.R;
import lk.tnm.eshop.databinding.FragmentCheckoutBinding;
import lk.tnm.eshop.model.CartItem;
import lk.tnm.eshop.model.Order;

public class CheckoutFragment extends Fragment {
    private FragmentCheckoutBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCheckoutBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uid = firebaseAuth.getCurrentUser().getUid();

        binding.checkoutBtnProceed.setOnClickListener(v -> {
            db.collection("users").document(uid).collection("cart")
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot qds) {
                            if (!qds.isEmpty()) {
                                List<CartItem> cartItems = qds.toObjects(CartItem.class);


                                Order order = new Order();
                                order.setOrderId(String.valueOf(System.currentTimeMillis()));
                                order.setUserId(uid);


                                String shipping_name = binding.shippingDetailsName.getText().toString();
                                String shipping_email = binding.shippingDetailsEmail.getText().toString();
                                String shipping_contact = binding.shippingDetailsContact.getText().toString();
                                String shipping_address1 = binding.shippingDetailsAddress1.getText().toString();
                                String shipping_address2 = binding.shippingDetailsAddress2.getText().toString();
                                String shipping_city = binding.shippingDetailsCity.getText().toString();
                                String shipping_postCode = binding.shippingDetailsPostcode.getText().toString();

                                Order.Address shippingAddress = Order.Address.builder()
                                        .name(shipping_name)
                                        .email(shipping_email)
                                        .contact(shipping_contact)
                                        .address1(shipping_address1)
                                        .address2(shipping_address2)
                                        .city(shipping_city)
                                        .postcode(shipping_postCode).build();

                                order.setShippingAddress(shippingAddress);

                                if (!binding.shippingDetailsCheckBilling.isChecked()) {
                                    String billing_name = binding.billingDetailsName.getText().toString();
                                    String billing_email = binding.billingDetailsEmail.getText().toString();
                                    String billing_contact = binding.billingDetailsContact.getText().toString();
                                    String billing_address1 = binding.billingDetailsAddress1.getText().toString();
                                    String billing_address2 = binding.billingDetailsAddress2.getText().toString();
                                    String billing_city = binding.billingDetailsCity.getText().toString();
                                    String billing_postCode = binding.billingDetailsPostcode.getText().toString();

                                    Order.Address billingAddress = Order.Address.builder()
                                            .name(billing_name)
                                            .email(billing_email)
                                            .contact(billing_contact)
                                            .address1(billing_address1)
                                            .address2(billing_address2)
                                            .city(billing_city)
                                            .postcode(billing_postCode).build();

                                    order.setBillingAddress(billingAddress);
                                }


                                List<Order.OrderItem> orderItems = new ArrayList<>();

                                for (CartItem cartItem : cartItems) {

                                    List<Order.OrderItem.Attribute> attributes = new ArrayList<>();

                                    for (CartItem.Attribute at : cartItem.getAttributes()) {
                                        Order.OrderItem.Attribute attribute = Order.OrderItem.Attribute.builder()
                                                .name(at.getName())
                                                .value(at.getValue())
                                                .build();

                                        attributes.add(attribute);
                                    }


                                    Order.OrderItem orderItem = Order.OrderItem.builder()
                                            .productId(cartItem.getProductId())
                                            .unitPrice(0)
                                            .quantity(cartItem.getQuantity())
                                            .attributes(attributes).build();

                                    orderItems.add(orderItem);

                                }


                                ///  Add order items to Oder object
                                order.setOrderItems(orderItems);


                                db.collection("orders").document()
                                        .set(order)
                                        .addOnSuccessListener(aVoid->{
                                            Toast.makeText(getContext(), "Order Saved!", Toast.LENGTH_SHORT).show();
                                        });

                            }


                        }
                    });
        });


    }

}