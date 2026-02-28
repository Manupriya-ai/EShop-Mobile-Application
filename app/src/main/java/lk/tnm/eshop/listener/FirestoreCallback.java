package lk.tnm.eshop.listener;

public interface FirestoreCallback<T> {
    void onCallback(T data);
}
