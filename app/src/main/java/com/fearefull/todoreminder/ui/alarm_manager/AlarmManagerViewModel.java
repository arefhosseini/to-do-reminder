package com.fearefull.todoreminder.ui.alarm_manager;

import android.net.Uri;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import com.fearefull.todoreminder.data.DataManager;
import com.fearefull.todoreminder.data.model.db.Alarm;
import com.fearefull.todoreminder.data.model.db.Repeat;
import com.fearefull.todoreminder.data.model.other.item.AlarmTitleItem;
import com.fearefull.todoreminder.data.model.other.item.RepeatItem;
import com.fearefull.todoreminder.data.model.other.type.AlarmTitleType;
import com.fearefull.todoreminder.ui.base.BaseViewModel;
import com.fearefull.todoreminder.utils.AlarmUtils;
import com.fearefull.todoreminder.utils.rx.SchedulerProvider;
import com.kevalpatel.ringtonepicker.RingtonePickerListener;

import java.util.List;
import java.util.Objects;

import timber.log.Timber;

public class AlarmManagerViewModel extends BaseViewModel<AlarmManagerNavigator> {

    private Alarm alarm;
    private Repeat selectedRepeat;
    private int defaultRepeatCount;

    private final MutableLiveData<List<RepeatItem>> repeatItemsLiveData;
    private final MutableLiveData<List<AlarmTitleItem>> alarmTitleItemsLiveData;

    private final ObservableField<String> titleString = new ObservableField<>();
    private final ObservableField<Integer> defaultImageResTitle = new ObservableField<>();
    private final ObservableField<String> ringtoneString = new ObservableField<>();
    private final ObservableField<String> repeatCounter = new ObservableField<>();
    private final MutableLiveData<Integer> currentTabPager;
    private final MutableLiveData<Integer> pageLimitPager;
    private boolean shouldUpdateAlarm = false;
    private boolean shouldExit = false;

    public AlarmManagerViewModel(DataManager dataManager, SchedulerProvider schedulerProvider) {
        super(dataManager, schedulerProvider);
        repeatItemsLiveData = new MutableLiveData<>();
        alarmTitleItemsLiveData = new MutableLiveData<>();
        currentTabPager = new MutableLiveData<>();
        pageLimitPager = new MutableLiveData<>();
        pageLimitPager.setValue(Repeat.getCount());
        repeatCounter.set("0");
    }

    private void fetchRepeatData() {
        getCompositeDisposable().add(AlarmUtils.getRepeatItems(alarm.getDefaultRepeat())
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(repeatItemsLiveData::postValue, Timber::e)
        );

        getCompositeDisposable().add(AlarmUtils.getAlarmTitleItems(alarm.getTitleType())
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(alarmTitleItemsLiveData::postValue, Timber::e)
        );
        defaultImageResTitle.set(alarm.getTitleType().getImageRes());

        setSelectedRepeat(alarm.getDefaultRepeat());
        defaultRepeatCount = alarm.getRepeatCount();
    }

    public MutableLiveData<List<RepeatItem>> getRepeatItemsLiveData() {
        return repeatItemsLiveData;
    }

    public MutableLiveData<List<AlarmTitleItem>> getAlarmTitleItemsLiveData() {
        return alarmTitleItemsLiveData;
    }

    public void onNavigationBackClick() {
        getNavigator().goBack();
    }

    public void onSaveClick() {
        if (alarm.getRepeatCount() > defaultRepeatCount) {
            if (shouldUpdateAlarm) {
                getCompositeDisposable().add(getDataManager()
                        .updateAlarm(alarm)
                        .subscribeOn(getSchedulerProvider().io())
                        .observeOn(getSchedulerProvider().ui())
                        .subscribe(result -> {
                            if (result)
                                getNavigator().save();
                        }, Timber::e)
                );
            }
            else {
                getCompositeDisposable().add(getDataManager()
                        .insertAlarm(alarm)
                        .subscribeOn(getSchedulerProvider().io())
                        .observeOn(getSchedulerProvider().ui())
                        .subscribe(result -> {
                            if (result)
                                getNavigator().save();
                        }, Timber::e)
                );
            }
        }
        else {
            shouldExit = true;
            getNavigator().getLastRepeat(selectedRepeat);
        }
    }

    public void onTitleTextChange(CharSequence s) {
        alarm.setTitle(s.toString());
    }

    Alarm getAlarm() {
        return alarm;
    }

    void setAlarm(Alarm alarm) {
        this.alarm = alarm;
    }

    public Repeat getSelectedRepeat() {
        return selectedRepeat;
    }

    public void setSelectedRepeat(Repeat repeat) {
        this.selectedRepeat = repeat;
    }

    void initAlarm() {
        if (alarm.getRepeatCount() > 0)
            shouldUpdateAlarm = true;
        setIsLoading(true);
        fetchRepeatData();
        updateAlarm();
        openDefaultRepeatFragment();
    }

    void updateAlarm() {
        updateTitleString(alarm.getTitle());
        updateRingtoneString();
        updateAddCounter(alarm.getRepeatCount());
        if (shouldExit)
            onSaveClick();
    }

    void updateTitleString(String title) {
        titleString.set(title);
    }

    void updateAlarmTitle(AlarmTitleType titleType) {
        alarm.setTitleType(titleType);
        updateTitleString(titleType.getText());
        updateTitleImageRes(titleType.getImageRes());
    }

    void updateTitleImageRes(int imageRes) {
        defaultImageResTitle.set(imageRes);
    }

    void openDefaultRepeatFragment() {
        currentTabPager.setValue(alarm.getDefaultRepeat().getValue());
        setIsLoading(false);
    }

    void updateRingtoneString() {
        ringtoneString.set(alarm.getRingtone());
    }

    void updateAddCounter(int counter) {
        if (counter == 0)
            getNavigator().clearBell();
        else if (Integer.parseInt(Objects.requireNonNull(repeatCounter.get())) == 0)
            getNavigator().createWithShakeBell();
        else if (counter > Integer.parseInt(Objects.requireNonNull(repeatCounter.get())))
            getNavigator().shakeBell();
        repeatCounter.set(String.valueOf(counter));
    }

    public ObservableField<String> getTitleString() {
        return titleString;
    }

    public ObservableField<Integer> getDefaultImageResTitle() {
        return defaultImageResTitle;
    }

    public ObservableField<String> getRingtoneString() {
        return ringtoneString;
    }

    public ObservableField<String> getRepeatCounter() {
        return repeatCounter;
    }

    public MutableLiveData<Integer> getCurrentTabPager() {
        return currentTabPager;
    }

    public MutableLiveData<Integer> getPageLimitPager() {
        return pageLimitPager;
    }

    /*DialogInterface.OnClickListener repeatPickerOnClickListener = (dialog, which) -> {
        if (Alarm.indexToRepeat(which) != Repeat.CUSTOM) {
            alarm.setRepeat(Alarm.indexToRepeat(which));
            dialog.dismiss();
            updateRepeatString();
        }
        else {
            getNavigator().openCustomRepeatPickerFragment();
        }
    };*/

    RingtonePickerListener ringtonePickerListener = (ringtoneName, ringtoneUri) -> {
        if (ringtoneUri != null) {
            alarm.setRingtone(ringtoneName);
            //alarm.getRingtoneString().setUri(ringtoneUri);
            updateRingtoneString();
        }
    };

    public void onRingtoneClick() {
        getNavigator().closeAllExpansions();
    }

    public void onRepeatManagerClick() {
        getNavigator().onShowRepeatManagerDialog();
    }

    Uri getDefaultRingtone() {
        return null;
    }
}
