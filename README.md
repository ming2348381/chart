# CharDemo


**CharDemo** - An Android custom char view that support attrs settings.

![CharDemo](https://raw.githubusercontent.com/ming2348381/chart/master/art/default.gif)


## Usage

* In XML layout:

```xml
    <com.bill.chart.BrokenLineView
        app:circleColor="@color/red"
        app:circleRadius="5dp"
        app:titleColumnColor="@color/black"
        app:titleColumnTextSize="18dp"
        app:lineStrokeWidth="3dp"
        app:lineColor="@android:color/holo_orange_dark"
        app:displayRowCount="8"
        android:layout_width="match_parent"
        android:layout_height="match_parent">
    </com.bill.chart.BrokenLineView>
```

![CharDemo](https://raw.githubusercontent.com/ming2348381/chart/master/art/xml_demo.png)

* All customizable attributes:

```xml
    <declare-styleable name="BrokenLineView">
        <attr name="circleColor" format="color" />
        <attr name="circleRadius" format="dimension" />
        <attr name="titleColumnColor" format="color" />
        <attr name="titleColumnStrokeWidth" format="dimension" />
        <attr name="titleColumnTextSize" format="dimension" />
        <attr name="lineColor" format="color" />
        <attr name="lineStrokeWidth" format="dimension" />
        <attr name="displayRowCount" format="integer" />
        <attr name="animationTotalTime" format="integer" />
        <attr name="animationInterval" format="integer" />
    </declare-styleable>
```