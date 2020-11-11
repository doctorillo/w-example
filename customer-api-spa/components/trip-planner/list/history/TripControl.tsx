import React from 'react'
import Button from '@material-ui/core/Button'
import Plus from '@material-ui/icons/Add'
import Box from '@material-ui/core/Box'

// props: TripPlannerFn

function TripHistoryControl() {
  return (
    <Box p={2}>
      <Button>
        <Plus color="primary" />
        Создать
      </Button>
    </Box>
  )
}

export default TripHistoryControl
