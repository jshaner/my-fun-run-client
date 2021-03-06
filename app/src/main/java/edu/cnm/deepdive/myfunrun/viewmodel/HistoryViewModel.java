package edu.cnm.deepdive.myfunrun.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.Lifecycle.Event;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ViewModel;
import edu.cnm.deepdive.myfunrun.model.entity.History;
import edu.cnm.deepdive.myfunrun.model.pojo.HistoryWithDetails;
import edu.cnm.deepdive.myfunrun.service.HistoryRepository;
import io.reactivex.disposables.CompositeDisposable;
import java.util.List;

/**
 * The type History view model.
 */
public class HistoryViewModel extends AndroidViewModel implements LifecycleObserver {

  private final HistoryRepository historyRepository;
  private final MutableLiveData<HistoryWithDetails> history;
  private final MutableLiveData<Throwable> throwable;
  private final CompositeDisposable pending;


  /**
   * Instantiates a new History view model.
   *
   * @param application the application
   */
  public HistoryViewModel(@NonNull Application application) {
    super(application);
    historyRepository = new HistoryRepository(application);
    history = new MutableLiveData<>();
    throwable = new MutableLiveData<>();
    pending = new CompositeDisposable();

  }

  /**
   * Gets history.
   *
   * @return the history
   */
  public LiveData<HistoryWithDetails> getHistory() {
    return history;
  }

  /**
   * Gets throwable.
   *
   * @return the throwable
   */
  public LiveData<Throwable> getThrowable() {
    return throwable;
  }

  /**
   * Gets histories.
   *
   * @return the histories
   */
  public LiveData<List<HistoryWithDetails>> getHistories() {
    return historyRepository.getAll();
  }

  /**
   * Sets history id.
   *
   * @param id the id
   */
  public void setHistoryId(long id) {
    throwable.setValue(null);
    pending.add(
        historyRepository.get(id)
            .subscribe(
                (history) -> this.history.postValue(history),
                (throwable) -> this.throwable.postValue(throwable)
            )
    );
  }

  /**
   * Save.
   *
   * @param history the history
   */
  public void save(History history) {
    throwable.setValue(null);
    pending.add(
        historyRepository.save(history)
            .subscribe(
                () -> {},
                (throwable) -> this.throwable.postValue(throwable)
            )
    );
  }

  /**
   * Delete.
   *
   * @param history the history
   */
  public void delete (History history) {
    throwable.setValue(null);
    pending.add(
        historyRepository.delete(history)
            .subscribe(
                () -> {},
                (throwable) -> this.throwable.postValue(throwable)
            )
    );
  }

  @OnLifecycleEvent(Event.ON_STOP)
  private void clearPending() {
    pending.clear();
  }
}