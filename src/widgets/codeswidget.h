#pragma once

#include "uvalib_global.h"
#include "uvaarenawidget.h"
#include <QWidget>

namespace uva
{

    namespace Ui {
        class CodesWidget;
    }

    class UVA_EXPORT CodesWidget : public UVAArenaWidget
    {
        Q_OBJECT

    public:
        explicit CodesWidget(QWidget *parent = 0);
        ~CodesWidget();

    private:
        Ui::CodesWidget *ui;
    };

}